package mobi.myseries.shared;

public class AsyncTaskResult<T> {
	private T result;
	private Exception error;

	public T result() {
		return result;
	}

	public Exception error() {
		return error;
	}

	public AsyncTaskResult(T result) {
		this.result = result;
	}

	public AsyncTaskResult(Exception error) {
		this.error = error;
	}
}
