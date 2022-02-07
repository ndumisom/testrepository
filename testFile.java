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
