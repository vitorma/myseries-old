package mobi.myseries.gui.shared;

import mobi.myseries.R;
import net.simonvt.menudrawer.MenuDrawer;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public abstract class BaseActivity extends Activity {

    private MenuDrawer menuDrawer;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        this.setUpMenu();
    }

    protected void setUpMenu() {
        this.menuDrawer = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_WINDOW);
        this.menuDrawer.setMenuView(R.layout.menu_frame);
        this.menuDrawer.setMenuSize((int) this.getResources().getDimension(R.dimen.sliding_menu_width));
        this.menuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);

        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        Fragment f = new SlidingMenuFragment();
        ft.replace(R.id.menu_frame, f);
        ft.commit();
    }

    protected MenuDrawer getMenu() {
        return this.menuDrawer;
    }

    protected void toggleMenu() {
        this.menuDrawer.toggleMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
