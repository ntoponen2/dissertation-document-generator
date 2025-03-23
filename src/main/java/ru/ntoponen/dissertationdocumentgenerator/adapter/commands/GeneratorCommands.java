package ru.ntoponen.dissertationdocumentgenerator.adapter.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.ntoponen.dissertationdocumentgenerator.app.api.generate.FileGenerator;
import ru.ntoponen.dissertationdocumentgenerator.app.api.FileReader;
import ru.ntoponen.dissertationdocumentgenerator.domain.doctoral.DoctoralData;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@ShellComponent
@RequiredArgsConstructor
public class GeneratorCommands {
    private final FileReader fileReader;
    private final FileGenerator fileGenerator;
    private final ObjectMapper objectMapper;

    @ShellMethod(key = "generate", value = "Generate files for dissertation using json data")
    public String generate(@ShellOption String fileName) {
        if (fileName == null || fileName.equals("")) {
            return "fileName is not defined";
        }
        try {
            Resource file = fileReader.getFileFromCurrentDirectory(fileName);
            DoctoralData data = objectMapper.readValue(file.getFile(), DoctoralData.class);
            fileGenerator.generateDoctoral(data);
            return data.toString();
        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
            return "Error while reading file: " + e.getMessage();
        }
    }
}
