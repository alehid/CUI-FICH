package com.gaston.ciudaduniversitariainteligente;

/*
 * Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * <p>Encapsulates the result of a barcode scan invoked through {@link IntentIntegrator}.</p>
 *
 * @author Sean Owen
 */
public final class IntentResult {
    // String utilizado para el tracking de errores
    private LogginCUI log = new LogginCUI();

    private String contents= null;
    private  String formatName= null;
    private  byte[] rawBytes= null;
    private  Integer orientation= null;
    private  String errorCorrectionLevel= null;

    IntentResult() {
        this(null, null, null, null, null);
    }

    IntentResult(String contents,  String formatName, byte[] rawBytes, Integer orientation, String errorCorrectionLevel) {
        try {

            this.contents = contents;
            this.formatName = formatName;
            this.rawBytes = rawBytes;
            this.orientation = orientation;
            this.errorCorrectionLevel = errorCorrectionLevel;
        }catch (Exception e){

            log.registrar(this,"IntentResult",e);
//            log.alertar("Ocurrió un error al momento de cargar el fragment Busqueda.",getActivity());

        }
    }

    /**
     * @return raw content of barcode
     */
    public String getContents() {
        return contents;
    }

    /**
     * @return name of format, like "QR_CODE", "UPC_A". See {@code BarcodeFormat} for more format names.
     */
    public String getFormatName() {
        return formatName;
    }

    /**
     * @return raw bytes of the barcode content, if applicable, or null otherwise
     */
    public byte[] getRawBytes() {
        return rawBytes;
    }

    /**
     * @return rotation of the image, in degrees, which resulted in a successful scan. May be null.
     */
    public Integer getOrientation() {
        return orientation;
    }

    /**
     * @return name of the error correction level used in the barcode, if applicable
     */
    public String getErrorCorrectionLevel() {
        return errorCorrectionLevel;
    }

    @Override
    public String toString() {
        String resultado = "";
        try {
            int rawBytesLength = rawBytes == null ? 0 : rawBytes.length;
            resultado=  "Format: " + formatName + '\n' +
                        "Contents: " + contents + '\n' +
                        "Raw bytes: (" + rawBytesLength + " bytes)\n" +
                        "Orientation: " + orientation + '\n' +
                        "EC level: " + errorCorrectionLevel + '\n';
        }catch (Exception e){

            log.registrar(this,"toString",e);


        }
        return resultado;
    }

}