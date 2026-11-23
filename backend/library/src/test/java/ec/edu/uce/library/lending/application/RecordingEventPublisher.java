package ec.edu.uce.library.lending.application;

import java.util.ArrayList;
import java.util.List;

class RecordingEventPublisher implements DomainEventPublisher {

    final List<Object> published = new ArrayList<>();

    @Override
    public void publish(Object event) {
        published.add(event);
    }
}
