package libs.observe;

public interface IObservable {
       void addObserver(IObserver observer);
	   void removeObserver(IObserver observer);
       void removeAllObservers();
	   void notifyObservers();
}
