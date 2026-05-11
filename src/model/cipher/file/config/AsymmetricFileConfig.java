package model.cipher.file.config;

import model.cipher.file.AFileAsymCipher;
import model.cipher.file.AFileSymCipher;

import java.io.File;

public class AsymmetricFileConfig {
    private final AFileAsymCipher asymCipher;
    private final AFileSymCipher symCipher;
    private final String asymMode;
    private final String asymPadding;
    private final String symMode;
    private final String symPadding;
    private final File selectedFile;

    public AsymmetricFileConfig(
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