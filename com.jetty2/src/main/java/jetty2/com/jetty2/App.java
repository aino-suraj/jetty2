package jetty2.com.jetty2;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.DatabaseAdaptor;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.HouseKeeper;
import org.eclipse.jetty.server.session.JDBCSessionDataStoreFactory;
import org.eclipse.jetty.server.session.SessionCache;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Hello world!
 *
 */
public class App {

  private static int deploymentPort = 8082;
  private static String driver = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
  private static String url = "jdbc:mysql://172.31.0.3:3306/sessions";
  private static String user = "root";
  private static String password = "mysql#htznr";

  public static void main(String[] args) throws Exception {
    Server server = new Server(deploymentPort);

    WebAppContext context = new WebAppContext();
    context.setContextPath("/");
    context.setResourceBase(System.getProperty("java.io.tmpdir"));
    server.setHandler(context);

    // Configure DefaultSessionIdManager
    configureDefaultSessionIdManager(server);

    // Add SqlSession handler
    addSqlSessionHandlerToContext(context);

    context.addServlet(HelloSessionServlet2.class, "/");
    server.start();
    server.join();
  }

  /**
   * Configure the Default session id manager.
   * 
   * @param server instance of server
   * @throws Exception
   */
  public static void configureDefaultSessionIdManager(Server server) throws Exception {
    DefaultSessionIdManager defaultSessionIdManager = new DefaultSessionIdManager(server);
    defaultSessionIdManager.setWorkerName("jetty2-Worker");

    HouseKeeper houseKeeper = new HouseKeeper();
    houseKeeper.setSessionIdManager(defaultSessionIdManager);
    // set the frequency of scavenge cycles
    houseKeeper.setIntervalSec(600L);
    defaultSessionIdManager.setSessionHouseKeeper(houseKeeper);

    server.setSessionIdManager(defaultSessionIdManager);
  }


  /**
   * Add Sql session handler to web app context.
   * 
   * @param context instance of {@link WebAppContext}
   */
  public static void addSqlSessionHandlerToContext(WebAppContext context) {
    SessionHandler sessionHandler = context.getSessionHandler();
    SessionCache sessionCache = new DefaultSessionCache(sessionHandler);
    sessionCache
        .setSessionDataStore(buildJdbcDataStoreFactory().getSessionDataStore(sessionHandler));
    sessionHandler.setSessionCache(sessionCache);
    sessionHandler.setHttpOnly(true);
  }

  /**
   * Build Jdbc Data store factory.
   * 
   * @return
   */
  public static JDBCSessionDataStoreFactory buildJdbcDataStoreFactory() {
    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setDataSourceClassName(driver);
    hikariConfig.addDataSourceProperty("url", url);
    hikariConfig.addDataSourceProperty("user", user);
    hikariConfig.addDataSourceProperty("password", password);
    HikariDataSource datasources = new HikariDataSource(hikariConfig);
    DatabaseAdaptor databaseAdaptor = new DatabaseAdaptor();
    databaseAdaptor.setDriverInfo(driver, url);
    databaseAdaptor.setDatasource(datasources);
    JDBCSessionDataStoreFactory jdbcSessionDataStoreFactory = new JDBCSessionDataStoreFactory();
    jdbcSessionDataStoreFactory.setDatabaseAdaptor(databaseAdaptor);
    return jdbcSessionDataStoreFactory;
  }



}
