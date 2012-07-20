package mobi.myseries.gui.appwidget;

public class MyScheduleWidgetServiceMedium extends MyScheduleWidgetServiceLarge {

    public MyScheduleWidgetServiceMedium() {
        super("mobi.myseries.gui.appwidget.MyScheduleWidgetServiceMedium");
    }

    @Override
    protected Class<MyScheduleWidgetProviderMedium> widgetClass() {
        return MyScheduleWidgetProviderMedium.class;
    }
}