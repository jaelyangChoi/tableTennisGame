package my.tableTennisGame.common;

public class WrongRequestException extends RuntimeException {
    public WrongRequestException(String message) {
        super(message);
    }
}
