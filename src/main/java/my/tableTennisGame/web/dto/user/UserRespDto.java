package my.tableTennisGame.web.dto.user;

import lombok.Getter;
import lombok.Setter;
import my.tableTennisGame.domain.user.User;
import org.springframework.data.domain.Page;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserRespDto {

    @Getter
    @Setter
    public static class UserListRespDto {
        private int totalElements;
        private int totalPages;
        private List<UserDto> userList = new ArrayList<>();

        public UserListRespDto(Page<User> userPage) {
            this.totalElements = (int) userPage.getTotalElements();
            this.totalPages = userPage.getTotalPages();
            this.userList = userPage.getContent().stream()
                    .map(UserDto::new).toList();
        }

        @Getter
        @Setter
        public static class UserDto {
            private int id;
            private int fakerId;
            private String name;
            private String email;
            private String status;
            private String cratedAt;
            private String updatedAt;

            public UserDto(User user) {
                this.id = user.getId();
                this.fakerId = user.getFakerId();
                this.name = user.getName();
                this.email = user.getEmail();
                this.status = user.getStatus().name();
                this.cratedAt = user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                this.updatedAt = user.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        }
    }
}
