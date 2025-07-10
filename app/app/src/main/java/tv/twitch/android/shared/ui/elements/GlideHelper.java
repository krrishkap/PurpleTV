package tv.twitch.android.shared.ui.elements;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spanned;
import android.widget.TextView;

import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import kotlin.collections.CollectionsKt;
import tv.purple.monolith.models.exception.VirtualImpl;
import tv.twitch.android.app.core.ActivityUtilKt;
import tv.twitch.android.shared.ui.elements.span.CenteredImageSpan;
import tv.twitch.android.shared.ui.elements.span.FixedTimeCoordinatorDrawable;
import tv.twitch.android.shared.ui.elements.span.GlideChatImageCustomTarget;
import tv.twitch.android.shared.ui.elements.span.GlideChatImageTarget;
import tv.twitch.android.shared.ui.elements.span.UrlDrawable;

public final class GlideHelper {
    public static final GlideHelper INSTANCE = new GlideHelper();

    /* ... */

    public final void clearPendingGlideLoad(Context context, Target<?> glideTarget) {
        throw new VirtualImpl();
    }

    private static GlideChatImageCustomTarget loadImageForUrlDrawableAngGetTarget(Context context, UrlDrawable urlDrawable, TextView textView) {
        GlideChatImageCustomTarget into = null;

        /* ... */

        into.setContainer(textView); // TODO: __INJECT_CODE

        /* ... */

        throw new VirtualImpl();
    }

    private static void loadImageForUrlDrawable(Context context, UrlDrawable urlDrawable, TextView textView) {
        GlideChatImageTarget into = null;

        /* ... */

        into.setContainer(textView); // TODO: __INJECT_CODE // FIXME

        /* ... */

        throw new VirtualImpl();
    }

    @Deprecated
    public static final void loadImagesFromSpanned(Context context, Spanned spanned, TextView textView) {
        if (ActivityUtilKt.isInvalid(context)) {
            return;
        }
        Object[] spans = spanned.getSpans(0, spanned.length(), CenteredImageSpan.class);
        for (Object obj : spans) {
            Drawable drawable = ((CenteredImageSpan) obj).getDrawable();
            if (drawable instanceof FixedTimeCoordinatorDrawable) {
                FixedTimeCoordinatorDrawable fixedTimeCoordinatorDrawable = (FixedTimeCoordinatorDrawable) drawable;
                if (fixedTimeCoordinatorDrawable.shouldDisplayInitialDrawable()) {
                    loadImageForUrlDrawableIter(context, fixedTimeCoordinatorDrawable.getInitialUrlDrawable(), textView); // TODO: __REPLACE_CODE
                }
                loadImageForUrlDrawableIter(context, fixedTimeCoordinatorDrawable.getFinalUrlDrawable(), textView); // TODO: __REPLACE_CODE
            } else if (drawable instanceof UrlDrawable) {
                loadImageForUrlDrawableIter(context, (UrlDrawable) drawable, textView); // TODO: __REPLACE_CODE
            }
        }
    }

    public static final List<GlideChatImageCustomTarget> loadImagesFromSpannedAndGetTargets(Context context, Spanned spanned, TextView textView) {
        if (ActivityUtilKt.isInvalid(context)) {
            return CollectionsKt.emptyList();
        }
        CenteredImageSpan[] centeredImageSpanArr = (CenteredImageSpan[]) spanned.getSpans(0, spanned.length(), CenteredImageSpan.class);
        ArrayList<GlideChatImageCustomTarget> arrayList = new ArrayList();
        for (CenteredImageSpan centeredImageSpan : centeredImageSpanArr) {
            Drawable drawable = centeredImageSpan.getDrawable();
            if (drawable instanceof FixedTimeCoordinatorDrawable) {
                FixedTimeCoordinatorDrawable fixedTimeCoordinatorDrawable = (FixedTimeCoordinatorDrawable) drawable;
                if (fixedTimeCoordinatorDrawable.shouldDisplayInitialDrawable()) {
                    arrayList.addAll(loadImageForUrlDrawableIter2(context, fixedTimeCoordinatorDrawable.getInitialUrlDrawable(), textView)); // TODO: __REPLACE_CODE
                }
                arrayList.addAll(loadImageForUrlDrawableIter2(context, fixedTimeCoordinatorDrawable.getFinalUrlDrawable(), textView)); // TODO: __REPLACE_CODE
            } else if (drawable instanceof UrlDrawable) {
                arrayList.addAll(loadImageForUrlDrawableIter2(context, (UrlDrawable) drawable, textView)); // TODO: __REPLACE_CODE
            }
        }
        return arrayList;
    }


    private static void loadImageForUrlDrawableIter(Context context, UrlDrawable drawable, TextView textView) { // TODO: __INJECT_METHOD
        loadImageForUrlDrawable(context, drawable, textView);
        for (UrlDrawable load : drawable.getStack()) {
            loadImageForUrlDrawableIter(context, load, textView);
        }
    }

    private static List<GlideChatImageCustomTarget> loadImageForUrlDrawableIter2(Context context, UrlDrawable drawable, TextView textView) { // TODO: __INJECT_METHOD
        ArrayList<GlideChatImageCustomTarget> arrayList = new ArrayList<>();
        arrayList.add(loadImageForUrlDrawableAngGetTarget(context, drawable, textView));
        for (UrlDrawable load : drawable.getStack()) {
            arrayList.addAll(loadImageForUrlDrawableIter2(context, load, textView));
        }

        return arrayList;
    }

    public static final class RequestOptionsBundle {

    }

    /* ... */
}
