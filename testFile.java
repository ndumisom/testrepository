    public ResponseEntity<byte[]> getMembershipCorrespondence(
            @ApiParam(name = "membershipNumber", value = "Membership number", required = true) @PathVariable final String membershipNumber,
            @ApiParam(name = "documentReference", value = "Document reference", required = true) @PathVariable final String documentReference,
            @ApiParam(name = "bypassEncryption", value = "bypassEncryption", defaultValue = "false") @RequestParam(required = false) boolean bypassEncryption) {

        if (membershipNumber == null || membershipNumber.trim().equals("")) {
            throw new ResourceNotFoundException(MessageCodeEnum.DOCUMENT_NOT_FOUND.getCode(),
                    localeService.getMessage(MessageCodeEnum.DOCUMENT_NOT_FOUND.getCode(),
                            new Object[]{String.valueOf(membershipNumber)}));
        }

        if (documentReference == null || documentReference.trim().equals("")) {
            throw new ResourceNotFoundException(MessageCodeEnum.DOCUMENT_NOT_FOUND.getCode(),
                    localeService.getMessage(MessageCodeEnum.DOCUMENT_NOT_FOUND.getCode(),
                            new Object[]{String.valueOf(documentReference)}));
        }
        membershipService.validateMembership(membershipNumber);

        za.co.mmiholdings.health.model.DocumentType document = correspondenceService.getCorrespondence(membershipNumber, documentReference, bypassEncryption);

        HttpHeaders headers = new HttpHeaders();

        // Content-Length header.
        headers.setContentLength(document.getData().length);

        headers.setContentType(MediaType.parseMediaType(document.getMimeType()));

        // Content-Disposition header.
        headers.setContentDispositionFormData("attachment",
                String.format("document-%s.%s", documentReference, document.getExtension()));

        return ResponseEntity.ok().headers(headers).body(document.getData());
    }




//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package za.co.mhg.enterprise.common.util.localization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MessageByLocaleServiceImpl implements MessageByLocaleService {
    @Autowired
    private MessageSource messageSource;

    public MessageByLocaleServiceImpl() {
    }

    public String getMessage(String code) {
        return this.messageSource.getMessage(code, (Object[])null, LocaleContextHolder.getLocale());
    }

    public String getMessage(String code, Object[] args) {
        return this.messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    public String getMessage(String code, String defaultMessage) {
        return this.messageSource.getMessage(code, (Object[])null, defaultMessage, LocaleContextHolder.getLocale());
    }
}





private za.co.mmiholdings.health.model.DocumentType downloadDocumentFromUrl(final String documentReference) {
        GetImageUrlResponse getImageUrlResponse =
                legacyRepository.processGetImageUrl(new GetImageUrlRequest(documentReference));

        if(getImageUrlResponse == null) {
            throw new ResourceNotFoundException(MessageCodeEnum.DOCUMENT_NOT_FOUND.getCode(),
                    localeService.getMessage(MessageCodeEnum.DOCUMENT_NOT_FOUND.getCode(),
                            new Object[]{String.valueOf(documentReference)}));
        }

        // stream to byte array.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;

        try {
            byte[] chunk = new byte[4096];
            int bytesRead;

            is = new URL(getImageUrlResponse.getModel().getImageUrl()).openStream();
            while((bytesRead = is.read(chunk)) > 0) {
                baos.write(chunk, 0, bytesRead);
            }

            return new za.co.mmiholdings.health.model.DocumentType(baos.toByteArray(), za.co.mmiholdings.health.model.DocumentType.DocumentFormat.valueOf(getImageUrlResponse.getModel().getImageType().toString()));
        }
        catch(IOException e) {
            LOGGER.error(String.format("Failure while reading bytes from '%s'",
                    getImageUrlResponse.getModel().getImageUrl()), e);

            throw new ResourceNotFoundException(MessageCodeEnum.DOCUMENT_NOT_FOUND.getCode(),
                    localeService.getMessage(MessageCodeEnum.DOCUMENT_NOT_FOUND.getCode(),
                            new Object[]{String.valueOf(documentReference)}));
        }
        finally {
            try {
                if(is != null) {
                    is.close();
                }
            }
            catch(IOException e) {
                LOGGER.warn("Failure while closing input stream", e);
            }

            try {
                baos.flush();
                baos.close();
            }
            catch(IOException e) {
                LOGGER.warn("Failure while flushing and closing output stream", e);
            }
        }
    }




//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package za.co.mmiholdings.health.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@JsonInclude(Include.NON_NULL)
@ApiModel
public class DocumentType {
    private byte[] data;
    private DocumentType.DocumentFormat format;
    private String mimeType;
    private String fileName;

    public DocumentType() {
    }

    public DocumentType(byte[] data, String mimeType) {
        this.data = data;
        this.mimeType = mimeType;
        this.format = this.deriveFormatFromMimeType(mimeType);
    }

    public DocumentType(byte[] data, DocumentType.DocumentFormat format) {
        this.data = data;
        this.format = format;
        this.mimeType = this.deriveMimeTypeFromFormat(format);
    }

    private DocumentType.DocumentFormat deriveFormatFromMimeType(String mimeType) {
        DocumentType.DocumentFormat format = null;
        byte var4 = -1;
        switch(mimeType.hashCode()) {
        case -1487394660:
            if (mimeType.equals("image/jpeg")) {
                var4 = 10;
            }
            break;
        case -1487103447:
            if (mimeType.equals("image/tiff")) {
                var4 = 15;
            }
            break;
        case -1248334925:
            if (mimeType.equals("application/pdf")) {
                var4 = 0;
            }
            break;
        case -1248332507:
            if (mimeType.equals("application/rtf")) {
                var4 = 4;
            }
            break;
        case -1248325150:
            if (mimeType.equals("application/zip")) {
                var4 = 3;
            }
            break;
        case -1071817359:
            if (mimeType.equals("application/vnd.ms-powerpoint")) {
                var4 = 16;
            }
            break;
        case -1050893613:
            if (mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                var4 = 6;
            }
            break;
        case -879272239:
            if (mimeType.equals("image/bmp")) {
                var4 = 5;
            }
            break;
        case -879264467:
            if (mimeType.equals("image/jpg")) {
                var4 = 9;
            }
            break;
        case -879258763:
            if (mimeType.equals("image/png")) {
                var4 = 13;
            }
            break;
        case -879255075:
            if (mimeType.equals("image/tif")) {
                var4 = 14;
            }
            break;
        case -825240547:
            if (mimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheetxls")) {
                var4 = 12;
            }
            break;
        case 381398759:
            if (mimeType.equals("application/vnd.ms-outlook")) {
                var4 = 17;
            }
            break;
        case 817335912:
            if (mimeType.equals("text/plain")) {
                var4 = 2;
            }
            break;
        case 904647503:
            if (mimeType.equals("application/msword")) {
                var4 = 8;
            }
            break;
        case 1176892386:
            if (mimeType.equals("image/x-icon")) {
                var4 = 7;
            }
            break;
        case 1316341873:
            if (mimeType.equals("message/rfc822")) {
                var4 = 1;
            }
            break;
        case 1993842850:
            if (mimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                var4 = 11;
            }
        }

        switch(var4) {
        case 0:
            format = DocumentType.DocumentFormat.PDF;
            break;
        case 1:
            format = DocumentType.DocumentFormat.EML;
            break;
        case 2:
            format = DocumentType.DocumentFormat.TXT;
            break;
        case 3:
            format = DocumentType.DocumentFormat.ZIP;
            break;
        case 4:
            format = DocumentType.DocumentFormat.RTF;
            break;
        case 5:
            format = DocumentType.DocumentFormat.BMP;
            break;
        case 6:
            format = DocumentType.DocumentFormat.DOCX;
            break;
        case 7:
            format = DocumentType.DocumentFormat.ICO;
            break;
        case 8:
            format = DocumentType.DocumentFormat.DOC;
            break;
        case 9:
            format = DocumentType.DocumentFormat.JPG;
            break;
        case 10:
            format = DocumentType.DocumentFormat.JPEG;
            break;
        case 11:
            format = DocumentType.DocumentFormat.XLSX;
            break;
        case 12:
            format = DocumentType.DocumentFormat.XLS;
            break;
        case 13:
            format = DocumentType.DocumentFormat.PNG;
            break;
        case 14:
            format = DocumentType.DocumentFormat.TIF;
            break;
        case 15:
            format = DocumentType.DocumentFormat.TIFF;
            break;
        case 16:
            format = DocumentType.DocumentFormat.PPT;
            break;
        case 17:
            format = DocumentType.DocumentFormat.MSG;
        }

        return format;
    }

    private String deriveMimeTypeFromFormat(DocumentType.DocumentFormat format) {
        String mimeType = "application/octet-stream";
        switch(format) {
        case PDF:
            mimeType = "application/pdf";
            break;
        case EML:
            mimeType = "message/rfc822";
            break;
        case TXT:
            mimeType = "text/plain";
            break;
        case ZIP:
            mimeType = "application/zip";
            break;
        case RTF:
            mimeType = "application/rtf";
            break;
        case BMP:
            mimeType = "image/bmp";
            break;
        case DOCX:
            mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            break;
        case ICO:
            mimeType = "image/x-icon";
            break;
        case DOC:
            mimeType = "application/msword";
            break;
        case JPG:
            mimeType = "image/jpg";
            break;
        case JPEG:
            mimeType = "image/jpeg";
            break;
        case XLSX:
            mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            break;
        case XLS:
            mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheetxls";
            break;
        case PNG:
            mimeType = "image/png";
            break;
        case TIF:
            mimeType = "image/tif";
            break;
        case TIFF:
            mimeType = "image/tiff";
            break;
        case PPT:
            mimeType = "application/vnd.ms-powerpoint";
            break;
        case MSG:
            mimeType = "application/vnd.ms-outlook";
        }

        return mimeType;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public DocumentType.DocumentFormat getFormat() {
        return this.format;
    }

    public void setFormat(DocumentType.DocumentFormat format) {
        this.format = format;
    }

    public void setFormat(String format) {
        this.format = DocumentType.DocumentFormat.valueOf(format.toUpperCase());
    }

    public String getExtension() {
        return this.format != null ? this.format.toString().toLowerCase() : null;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        if (this.format == null) {
            if (fileName.contains(".")) {
                String extension = fileName.substring(fileName.lastIndexOf(46) + 1);
                this.format = DocumentType.DocumentFormat.valueOf(extension.toUpperCase());
            }

            if (this.format == null) {
                this.format = DocumentType.DocumentFormat.TXT;
            }
        }

        if (this.mimeType == null) {
            this.mimeType = this.deriveMimeTypeFromFormat(this.format);
        }

    }

    public String toString() {
        return (new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)).append("data", this.data).append("format", this.format).toString();
    }

    public static enum DocumentFormat {
        EML,
        PDF,
        RTF,
        BMP,
        DOCX,
        DOC,
        ICO,
        JPG,
        JPEG,
        XLSX,
        XLS,
        PNG,
        TIF,
        TIFF,
        PPT,
        TXT,
        ZIP,
        MSG;

        private DocumentFormat() {
        }
    }
}
