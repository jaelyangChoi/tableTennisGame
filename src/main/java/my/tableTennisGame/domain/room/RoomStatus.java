package my.tableTennisGame.domain.room;

/**
 * Room 상태
 * 초기 -> WAIT
 * 조건이 맞아 게임 진행 시 -> PROGRESS
 * 게임 종료 시, HOST 가 방에서 나갈 경우 -> FINISH
 */
public enum RoomStatus {
    WAIT, PROGRESS, FINISH;
}
