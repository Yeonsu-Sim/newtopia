package io.ssafy.p.i13c203.gameserver.global.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.seed")
@Getter @Setter
public class AppSeedProperties {
    private Boolean enabled = true;
    private String overwrite;
    private Group npc = new Group();
    private Group ending = new Group();

    @Getter @Setter
    public static class Group {
        private Boolean enabled = false;
        private String overwrite = "never";  // never | if_changed | always
        private String location;         // classpath:/ file:/ 패턴
        private String matchBy = "code";  // code | id
        private Storage storage = new Storage();
    }
    @Getter @Setter
    public static class Storage { private String domain; }
}
