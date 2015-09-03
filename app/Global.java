import models.Mybatis;
import models.UserSessions;
import org.apache.ibatis.session.SqlSession;
import play.Application;
import play.GlobalSettings;
import play.Logger;

import org.apache.ibatis.session.SqlSessionFactory;
import scala.App;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Global extends GlobalSettings {
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void onStart(Application app) {
        super.onStart(app);
        Logger.info("on start");
        Mybatis.dbDrop();
        Mybatis.dbSync();

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Logger.info("delete expired");
                UserSessions.deleteExpired();
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    public void onStop(Application app) {
        scheduler.shutdown();
        super.onStop(app);
        Logger.info("on stop");
    }
}
