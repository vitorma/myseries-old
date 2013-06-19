package mobi.myseries.test.unit.application.notification;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import mobi.myseries.application.notification.DeterminateProgressNotification;
import mobi.myseries.application.notification.IndeterminateProgressNotification;
import mobi.myseries.application.notification.Notification;
import mobi.myseries.application.notification.NotificationDispatcher;
import mobi.myseries.application.notification.TextOnlyNotification;

public class NotificationDispatchingTest {

    private Notification tONotificationReference = new TextOnlyNotification();
    private TextOnlyNotification tOTextOnlyReference = new TextOnlyNotification();

    private Notification iPNotificationReference = new IndeterminateProgressNotification();
    private IndeterminateProgressNotification iPIndeterminateProgressReference = new IndeterminateProgressNotification();

    private Notification dPNotificationReference = new DeterminateProgressNotification();
    private DeterminateProgressNotification dPDeterminateProgressReference = new DeterminateProgressNotification();

    private class TestNotificationDispatcher extends NotificationDispatcher {
        public boolean textOnlyCalled = false;
        public boolean indeterminateProgressCalled = false;
        public boolean determinateProgressCalled = false;

        @Override
        public void notifyTextOnlyNotification(TextOnlyNotification notification) {
            this.textOnlyCalled = true;
        }

        @Override
        public void notifyIndeterminateProgressNotification(IndeterminateProgressNotification notification) {
            this.indeterminateProgressCalled = true;
        }

        @Override
        public void notifyDeterminateProgressNotification(DeterminateProgressNotification notification) {
            this.determinateProgressCalled = true;
        }

        @Override
        public void cancel(Notification notification) {
            // TODO Auto-generated method stub
            
        }
    }

    private TestNotificationDispatcher dispatcher;

    @Before
    public void setUp() {
        this.dispatcher = new TestNotificationDispatcher();
    }

    // Text only

    @Test
    public void tONotificationReference() {
        this.dispatcher.notify(this.tONotificationReference);
        assertTrue(this.dispatcher.textOnlyCalled);
    }

    @Test
    public void tOTextOnlyReference() {
        this.dispatcher.notify(this.tOTextOnlyReference);
        assertTrue(this.dispatcher.textOnlyCalled);
    }

    // Indeterminate progress

    @Test
    public void iPNotificationReference() {
        this.dispatcher.notify(this.iPNotificationReference);
        assertTrue(this.dispatcher.indeterminateProgressCalled);
    }

    @Test
    public void iPIndeterminateProgressReference() {
        this.dispatcher.notify(this.iPIndeterminateProgressReference);
        assertTrue(this.dispatcher.indeterminateProgressCalled);
    }

    // Determinate progress

    @Test
    public void dPNotificationReference() {
        this.dispatcher.notify(this.dPNotificationReference);
        assertTrue(this.dispatcher.determinateProgressCalled);
    }

    @Test
    public void dPIndeterminateProgressReference() {
        this.dispatcher.notify(this.dPDeterminateProgressReference);
        assertTrue(this.dispatcher.determinateProgressCalled);
    }
}
