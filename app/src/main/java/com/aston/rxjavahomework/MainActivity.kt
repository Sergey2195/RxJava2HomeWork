package com.aston.rxjavahomework

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val textChangedPublishSubject = PublishSubject.create<String>()
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val editText = findViewById<EditText>(R.id.editText)
        editText.addTextChangedListener(getTextWatcher())
        setupObserver()
    }

    private fun setupObserver() {
        val disposable = textChangedPublishSubject
            .debounce(2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ str ->
                Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
            }, {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }, {})
        compositeDisposable.add(disposable)
    }

    private fun getTextWatcher(): TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            textChangedPublishSubject.onNext(s.toString())
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}