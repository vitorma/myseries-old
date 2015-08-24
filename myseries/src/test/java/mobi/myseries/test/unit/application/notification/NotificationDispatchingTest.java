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

    private static final int ID = 0;
    private static final String MESSAGE = "Message!";
    private static final int CURRENT_PROGRESS = 1;
    private static final int TOTAL_PROGRESS = 2;

    private Notification tONotificationReference = new TextOnlyNotification(ID, MESSAGE);
    private TextOnlyNotification tOTextOnlyReference = new TextOnlyNotification(ID, MESSAGE);

    private Notification iPNotificationReference = new IndeterminateProgressNotification(ID, MESSAGE);
    private IndeterminateProgressNotification iPIndeterminateProgressReference = new IndeterminateProgressNotification(ID, MESSAGE);

    private Notification dPNotificationReference = new DeterminateProgressNotification(ID, MESSAGE, CURRENT_PROGRESS, TOTAL_PROGRESS);
    private DeterminateProgressNotification dPDeterminateProgressReference = new DeterminateProgressNotification(ID, MESSAGE, CURRENT_PROGRESS, TOTAL_PROGRESS);

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
        public void cancel(Notification notification) {}
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
