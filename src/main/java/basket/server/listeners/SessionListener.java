package basket.server.listeners;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import org.springframework.stereotype.Component;

@Component
public class SessionListener implements HttpSessionBindingListener {

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        System.out.println(event.getName());
        System.out.println(event.getValue());
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        System.out.println(event.getName());
        System.out.println(event.getValue());
    }
}
