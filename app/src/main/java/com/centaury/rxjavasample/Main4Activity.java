package com.centaury.rxjavasample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class Main4Activity extends AppCompatActivity {

    /**
     * Basic Observable, Observer, Subscriber example
     * Introduced CompositeDisposable and DisposableObserver
     * The observable emits custom data type (Note) instead of primitive data types
     * ----
     * .map() operator is used to turn the note into all uppercase letters
     * ----
     * You can also notice we got rid of the below declarations
     * Observable<Note> notesObservable = getNotesObservable();
     * DisposableObserver<Note> notesObserver = getNotesObserver();
     */

    private static final String TAG = Main4Activity.class.getSimpleName();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        // add to Composite observable
        // .map() operator is used to turn the note into all uppercase letters
        compositeDisposable.add(getNoteObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Note, Note>() {
                    @Override
                    public Note apply(Note note) throws Exception {
                        // Making the note to all uppercase
                        note.setNote(note.getNote().toUpperCase());
                        return note;
                    }
                })
                .subscribeWith(getNoteObserver()));
    }

    private DisposableObserver<Note> getNoteObserver() {
        return new DisposableObserver<Note>() {
            @Override
            public void onNext(Note note) {
                Log.d(TAG, "onNext: " + note.getNote());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        };
    }

    private Observable<Note> getNoteObservable() {
        final List<Note> notes = prepareNote();

        return Observable.create(new ObservableOnSubscribe<Note>() {
            @Override
            public void subscribe(ObservableEmitter<Note> emitter) throws Exception {
                for (Note note : notes){
                    if (!emitter.isDisposed()){
                        emitter.onNext(note);
                    }
                }
                if (!emitter.isDisposed()){
                    emitter.onComplete();
                }
            }
        });
    }

    private List<Note> prepareNote() {
        List<Note> notes = new ArrayList<>();
        notes.add(new Note(1, "buy tooth paste!"));
        notes.add(new Note(2, "call brother!"));
        notes.add(new Note(3, "watch narcos tonight!"));
        notes.add(new Note(4, "pay power bill!"));

        return notes;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeDisposable.clear();
    }
}
