package mobi.myseries.application.search;

import java.util.List;

import mobi.myseries.application.ApplicationService;
import mobi.myseries.application.Environment;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.source.trakttv.SearchSource;

public class SearchService extends ApplicationService<SearchListener> {

    public SearchService(Environment environment) {
        super(environment);
    }

    public void search(String query) {
        this.run(new SearchTask(query));
    }

    private void notifyOnStart() {
        this.runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (SearchListener l : SearchService.this.listeners()) {
                    l.onStart();
                }
            }
        });
    }

    private void notifyOnSucess(final List<SearchResult> list) {
        this.runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (SearchListener l : SearchService.this.listeners()) {
                    l.onSucess(list);
                }
            }
        });
    }

    private void notifyOnFailure(final Exception failure) {
        this.runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (SearchListener l : SearchService.this.listeners()) {
                    l.onFailure(failure);
                }
            }
        });
    }

    private void notifyOnFinish() {
        this.runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (SearchListener l : SearchService.this.listeners()) {
                    l.onFinish();
                }
            }
        });
    }

    private class SearchTask implements Runnable {
        private final String query;

        private SearchTask(String query) {
            this.query = query;
        }

        @Override
        public void run() {
            SearchService.this.notifyOnStart();

            try {
                SearchSource source = SearchService.this.environment().searchSource();
                List<SearchResult> result = source.search(this.query);

                SearchService.this.notifyOnSucess(result);
            } catch (Exception e) {
                SearchService.this.notifyOnFailure(e);
            }

            SearchService.this.notifyOnFinish();
        }
    }
}
