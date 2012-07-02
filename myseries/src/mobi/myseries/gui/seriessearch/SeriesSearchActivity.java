/*
 *   SeriesSearchActivity.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

package mobi.myseries.gui.seriessearch;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SearchSeriesListener;
import mobi.myseries.domain.model.Series;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class SeriesSearchActivity extends SherlockListActivity {
    private StateHolder state;
    private SearchSeriesListener listener;
    
    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        this.setContentView(R.layout.seriessearch);
        
        ActionBar ab = this.getSupportActionBar();
        ab.setTitle(R.string.search_series);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        setSupportProgressBarIndeterminateVisibility(false);
        
        this.setUpSearchListener();
        this.setupSearchButtonClickListener();
        this.setupItemClickListener();
        this.setupSearchFieldActionListeners();

        Object retained = getLastNonConfigurationInstance();
        if (retained != null && retained instanceof StateHolder) {
          state = (StateHolder) retained;
          loadState();
        } else {
          state = new StateHolder();
        }
    }

    private void loadState() {
        
        if(state.isSearching){
              if(App.getLastValidSearchResult() != null){
                  setupListOnAdapter(App.getLastValidSearchResult());
              }
              listener.onStart();
              App.registerSearchSeriesListener(listener);
              
              } else {
                  if(state.seriesFound != null)
                      setupListOnAdapter(state.seriesFound);
          
                  if(state.isShowingDialog)
                      state.dialog.show();
                  }
    }
    
    @Override
    public Object onRetainNonConfigurationInstance() {
       return state;
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        App.deregisterSearchSeriesListener(listener);
        if(state.dialog != null && state.dialog.isShowing()) {
            state.dialog.dismiss();
            state.isShowingDialog = true;
          }else{
              state.isShowingDialog = false;
          }
        
   }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupSearchButtonClickListener() {
        final ImageButton searchButton = (ImageButton) this.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                performSearch();
            }
        });
    }
    
    private void setUpSearchListener(){
        final EditText searchField = (EditText) SeriesSearchActivity.this.findViewById(R.id.searchField);
        final ImageButton searchButton = (ImageButton) this.findViewById(R.id.searchButton);
       
        this.listener =  new SearchSeriesListener() {

            @Override
            public void onSucess(List<Series> series) {
                state.seriesFound = series;
                setupListOnAdapter(series);
            }
          
            
            @Override
            public void onFaluire(Throwable exception) {
                Dialog dialog = new AlertDialog.Builder(SeriesSearchActivity.this)
                .setMessage(exception.getMessage())
                .create();
                dialog.show();
                state.dialog = dialog;
            }

            @Override
            public void onStart() {
               state.isSearching = true;
               setSupportProgressBarIndeterminateVisibility(true);
               searchField.setEnabled(false);
               searchButton.setEnabled(false);
            }

            @Override
            public void onFinish() {
                state.isSearching = false;
                setSupportProgressBarIndeterminateVisibility(false);
                searchField.setEnabled(true);
                searchButton.setEnabled(true);
            }
        };
    }

    private void performSearch() {
        final EditText searchField = (EditText) SeriesSearchActivity.this.findViewById(R.id.searchField);
        
        SeriesSearchActivity.this.setListAdapter(null);
        state.seriesFound = null;
        
        App.registerSearchSeriesListener(listener);
        App.searchSeries(searchField.getText().toString());
    }
    
    private void setupListOnAdapter(List<Series> series) {
        ArrayAdapter<Series> adapter = new SeriesSearchItemAdapter(//TODO Use a simple ArrayAdapter<String> and use StateHolder#seriesFound to recover series
                SeriesSearchActivity.this,
                SeriesSearchActivity.this,
                R.layout.seriessearch_item,
                series);
        SeriesSearchActivity.this.setListAdapter(adapter);
    }

    private void setupSearchFieldActionListeners() {
        final EditText searchField = (EditText) findViewById(R.id.searchField);

        searchField.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    performSearch();
                    return true;
                }

                return false;
            }
        });

        searchField.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    performSearch();
                }
                return false;
            }
        });

    }

    /**
     * Sets up a listener to item click events.
     */
    private void setupItemClickListener() {
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /** Current selected item. */
            private Series selectedItem;

            /** Custom dialog to show the overview of a series. */
            private Dialog dialog;

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                this.selectedItem = (Series) parent.getItemAtPosition(position);

                this.dialog = new Dialog(SeriesSearchActivity.this);
                this.dialog.setContentView(R.layout.seriessearch_following);

                this.updateDialogText();
                this.setBackButtonClickListener();
                this.setFollowButtonClickListener();

                this.dialog.show();
                state.dialog = this.dialog;
            }

            /**
             * Sets a listener to 'Back' button click events.
             */
            private void setBackButtonClickListener() {
                final Button backButton = (Button) this.dialog.findViewById(R.id.backButton);

                backButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        dialog.dismiss();
                    }
                });
            }

            /**
             * Sets a listener to 'Follow' button click events.
             */
            // TODO Auto-generated method stub

            private void setFollowButtonClickListener() {
                final Button followButton = (Button) this.dialog.findViewById(R.id.followButton);

                final boolean userFollowsSeries = App.follows(this.selectedItem);

                if (userFollowsSeries) {
                    followButton.setText(R.string.stop_following);
                } else {
                    followButton.setText(R.string.follow);
                }

                followButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (userFollowsSeries) {
                            App.stopFollowing(selectedItem);
                        } else {
                            App.follow(selectedItem);

                            String message = String.format(SeriesSearchActivity.this
                                    .getString(R.string.follow_successfull_message_format), selectedItem.name());

                            this.showToastWith(message);
                        }

                        dialog.dismiss();
                    }

                    private void showToastWith(String message) {
                        Toast toast = Toast.makeText(App.environment().context(), message,
                                Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }

            /**
             * Updates the overviewTextView and the title of the dialog.
             */
            private void updateDialogText() {
                TextView seriesOverview = (TextView) this.dialog
                        .findViewById(R.id.overviewTextView);

                this.dialog.setTitle(this.selectedItem.name());
                seriesOverview.setText(this.selectedItem.overview());
            }
        });
    }

    @Override
    public final boolean onSearchRequested() {
        final EditText searchField = (EditText) SeriesSearchActivity.this.findViewById(R.id.searchField);
        searchField.requestFocus();
        return true;
    }

    @Override
    public final void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        this.onSearchRequested();
    }

    private static class StateHolder {
        public boolean isSearching;
        Dialog dialog;
        boolean isShowingDialog;
        List<Series> seriesFound;
        public StateHolder() {}
      }
}
