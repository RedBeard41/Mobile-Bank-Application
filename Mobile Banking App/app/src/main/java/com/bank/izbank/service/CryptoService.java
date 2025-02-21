package com.bank.izbank.service;

import com.bank.izbank.MainScreen.FinanceScreen.CryptoModel;

import java.util.List;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CryptoService {
    private static final String BASE_URL = "https://api.nomics.com/v1/";
    private final ICryptoAPI api;

    public CryptoService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        api = retrofit.create(ICryptoAPI.class);
    }

    public Single<List<CryptoModel>> getCryptoData() {
        return api.getData();
    }
} 