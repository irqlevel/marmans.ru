import models.Mybatis;
import org.apache.ibatis.session.SqlSession;
import play.Application;
import play.GlobalSettings;
import play.Logger;

import org.apache.ibatis.session.SqlSessionFactory;
import scala.App;

public class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        super.onStart(app);
        Logger.info("on start");
        Mybatis.dbSync();
    }

    public void onStop(Application app) {
        super.onStop(app);
        Logger.info("on stop");
    }
}
