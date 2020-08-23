import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.InjectAccessors;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import com.oracle.svm.core.jdk.JDK11OrLater;
import java.io.IOException;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.function.BooleanSupplier;

// https://github.com/quarkusio/quarkus/pull/7587
@Substitute
@TargetClass(
    className = "sun.nio.ch.WindowsAsynchronousFileChannelImpl",
    innerClass = "DefaultIocpHolder",
    onlyWith = {JDK11OrLater.class, IsWindows.class})
final class Target_sun_nio_ch_WindowsAsynchronousFileChannelImpl_DefaultIocpHolder {

  @Alias
  @InjectAccessors(DefaultIocpAccessor.class)
  static Target_sun_nio_ch_Iocp defaultIocp;
}

@TargetClass(
    className = "sun.nio.ch.Iocp",
    onlyWith = {JDK11OrLater.class, IsWindows.class})
final class Target_sun_nio_ch_Iocp {

  @Alias
  Target_sun_nio_ch_Iocp(AsynchronousChannelProvider provider, Target_sun_nio_ch_ThreadPool pool)
      throws IOException {}

  @Alias
  Target_sun_nio_ch_Iocp start() {
    return null;
  }
}

@TargetClass(
    className = "sun.nio.ch.ThreadPool",
    onlyWith = {JDK11OrLater.class, IsWindows.class})
final class Target_sun_nio_ch_ThreadPool {

  @Alias
  static Target_sun_nio_ch_ThreadPool createDefault() {
    return null;
  }
}

final class DefaultIocpAccessor {
  static Target_sun_nio_ch_Iocp get() {
    try {
      return new Target_sun_nio_ch_Iocp(null, Target_sun_nio_ch_ThreadPool.createDefault()).start();
    } catch (IOException ioe) {
      throw new InternalError(ioe);
    }
  }
}

final class IsWindows implements BooleanSupplier {
  @Override
  public boolean getAsBoolean() {
    // Contains "Linux" or "Windows Server 2019"
    String os = System.getProperty("os.name");
    System.out.println("System property os.name is " + os);
    return os.contains("Windows");
  }
}

public class JdkSubstitutions {}
