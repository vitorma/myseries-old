package mobi.myseries.gui.appwidget;

public class MyScheduleWidgetServiceMedium extends MyScheduleWidgetServiceLarge {
    public MyScheduleWidgetServiceMedium() {
        super("mobi.myseries.gui.appwidget.MyScheduleWidgetProviderMedium$MyScheduleWidgetServiceMedium");
    }

    @Override
    protected Class widgetClass() {
        return MyScheduleWidgetProviderMedium.class;
    }
}