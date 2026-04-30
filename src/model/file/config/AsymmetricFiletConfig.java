package model.file.config;

import model.file.AFileAsymCipher;
import model.file.AFileSymCipher;

import java.io.File;

public class AsymmetricFiletConfig {
    private final AFileAsymCipher asymCipher;
    private final AFileSymCipher symCipher;
    private final String asymMode;
    private final String asymPadding;
    private final String symMode;
    private final String symPadding;
    private final File selectedFile;

    public AsymmetricFiletConfig(
            AFileAsymCipher asymCipher,
            AFileSymCipher symCipher,
            String asymMode,
            String asymPadding,
            String symMode,
            String symPadding,
            File selectedFile
    ) {
        this.asymCipher = asymCipher;
        this.symCipher = symCipher;
        this.asymMode = asymMode;
        this.asymPadding = asymPadding;
        this.symMode = symMode;
        this.symPadding = symPadding;
        this.selectedFile = selectedFile;
    }

    public AFileAsymCipher getAsymCipher() {
        return asymCipher;
    }

    public AFileSymCipher getSymCipher() {
        return symCipher;
    }

    public String getAsymMode() {
        return asymMode;
    }

    public String getAsymPadding() {
        return asymPadding;
    }

    public String getSymMode() {
        return symMode;
    }

    public String getSymPadding() {
        return symPadding;
    }

    public File getSelectedFile() {
        return selectedFile;
    }
}