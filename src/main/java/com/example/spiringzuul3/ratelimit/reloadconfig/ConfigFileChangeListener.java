package com.example.spiringzuul3.ratelimit.reloadconfig;

import com.example.spiringzuul3.ratelimit.config.properties.RateLimitProperties;
import com.example.spiringzuul3.ratelimit.support.StringToMatchTypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.boot.devtools.filewatch.ChangedFile;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConfigFileChangeListener implements FileChangeListener {

    @Value("${application.reload.file}")
    String originalFileLocation;

    final RateLimitProperties rateLimitProperties;

    final StringToMatchTypeConverter stringToMatchTypeConverter;


    @PostConstruct
    public void init() throws URISyntaxException {
        log.info("Config location: {}", originalFileLocation);
        Path path = Paths.get(originalFileLocation);
        if (!Files.isRegularFile(path)) {
            log.error("Not an external file - FileSystemWatcher not applied - file {}", originalFileLocation);
            return;
        }
        FileSystemWatcher fileSystemWatcher = new FileSystemWatcher(
                true, Duration.ofSeconds(60), Duration.ofSeconds(1));
        fileSystemWatcher.addSourceDirectory(path.getParent().toFile());
        fileSystemWatcher.addListener(this);
        fileSystemWatcher.start();
    }

    @Override
    public void onChange(Set<ChangedFiles> changeSet) {
        final String propertySourceName = "externalConfiguration";
        Optional<File> result = getConfigFileIfPresentAndChanged(changeSet);

        result.ifPresent(file -> {
            log.info("Loading new properties from file: {}", file);
            YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
            factoryBean.setResources(new FileSystemResource(originalFileLocation));
            Properties properties = factoryBean.getObject();
            ConfigurationPropertySource propertySource = new MapConfigurationPropertySource(properties);
            DefaultConversionService service = new DefaultConversionService();
            service.addConverter(stringToMatchTypeConverter);
            Binder binder = new Binder(Collections.singletonList(propertySource), new PropertySourcesPlaceholdersResolver(Collections.emptyList()), service);
            RateLimitProperties changedRateLimitProperties = binder.bind(RateLimitProperties.PREFIX, RateLimitProperties.class).get();
            this.rateLimitProperties.updatePolicyList(changedRateLimitProperties.getPolicyList());
        });
    }

    private Optional<File> getConfigFileIfPresentAndChanged(Set<ChangedFiles> changeSet) {
        return changeSet.stream().map(ChangedFiles::getFiles)
                .flatMap(Collection::stream)
                .filter(file -> originalFileLocation.contains(file.getRelativeName()))
                .map(ChangedFile::getFile)
                .findAny();
    }
}
