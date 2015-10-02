package api;

public class AppException extends Exception {
    private AppResult result = null;

    public AppResult getResult() {
        return result;
    }

    public AppException(int resultCode) {
        this.result = AppResult.error(resultCode);
    }

    public AppException(int resultCode, String resultDesc) {
        this.result = AppResult.error(resultCode);
        this.result.setResultDesc(resultDesc);
    }
}
