package ru.sergeykamyshov.weekplanner.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import java.util.Random;

import io.realm.Realm;
import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.data.db.model.Card;

public class CardUtils {

    @SuppressLint("ResourceType")
    public static String getCardColor(Context context, String cardId) {
        String color = getCardColorFromRealm(cardId);
        // null будет если это старая карточка без выбранного цвета
        if (color == null || color.isEmpty()) {
            return context.getResources().getString(R.color.card_color_default);
        }
        return color;
    }

    @SuppressLint("ResourceType")
    public static String getCardColor(Context context, String cardId, boolean newCard) {
        if (newCard) {
            boolean generateRandomColor = getGenerateColorPref(context);
            if (generateRandomColor) {
                String[] colors = context.getResources().getStringArray(R.array.card_color_entries);
                return colors[new Random().nextInt(colors.length)];
            } else {
                return context.getResources().getString(R.color.card_color_default);
            }
        } else {
            return getCardColor(context, cardId);
        }
    }

    private static String getCardColorFromRealm(String cardId) {
        Card card = Realm.getDefaultInstance().where(Card.class).equalTo("id", cardId).findFirst();
        if (card != null) {
            return card.getColor();
        }
        return Const.EMPTY;
    }

    private static boolean getGenerateColorPref(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("pref_generate_card_color", true);
    }
}
