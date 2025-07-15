package tv.twitch.android.shared.ui.elements.span;

import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import tv.twitch.android.app.core.Utility;

@SuppressWarnings("deprecation")
public class UrlDrawable extends BitmapDrawable {
    private Drawable drawable;
    private boolean isDestroyed;
    private Function1<? super Rect, Unit> onBoundsChangeListener;
    private final MediaSpan$Type type;
    private final String url;

    // Injecting fields
    private boolean drawWide = false; // TODO: __INJECT_FIELD
    private boolean drawAnimation = true; // TODO: __INJECT_FIELD
    private boolean grey = false; // TODO: __INJECT_FIELD
    private static final ColorMatrix GREY_MATRIX = new ColorMatrix(); // TODO: __INJECT_FIELD
    private static final ColorMatrixColorFilter GREY_FILTER = new ColorMatrixColorFilter(GREY_MATRIX); // TODO: __INJECT_FIELD
    private final List<UrlDrawable> stack = new ArrayList<>(0); // TODO: __INJECT_FIELD
    private boolean isFirstDraw = true;
    private boolean isAnimatable = false;

    static {
        GREY_MATRIX.setSaturation(0); // TODO: __INJECT_CODE
    }

    public List<UrlDrawable> getStack() { // TODO: __INJECT_METHOD
        return stack;
    }

    public void addToStack(List<UrlDrawable> drawables) { // TODO: __INJECT_METHOD
        stack.addAll(drawables);
    }

    public UrlDrawable() {
        this(null, null, 3, null);
    }

    public String getUrl() {
        return this.url;
    }

    public UrlDrawable(String str, MediaSpan$Type mediaSpan$Type, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? "" : str, (i & 2) != 0 ? MediaSpan$Type.Emote : mediaSpan$Type);
    }

    public UrlDrawable(String url, MediaSpan$Type type) {
        this.url = url;
        this.type = type;
    }

    public boolean isWide() { // TODO: __INJECT_METHOD
        return drawWide;
    }

    public void setWide(boolean wide) { // TODO: __INJECT_METHOD
        this.drawWide = wide;
    }

    public void setAnimated(boolean animated) { // TODO: __INJECT_METHOD
        this.drawAnimation = animated;
    }

    public void setDrawable(Drawable drawable) { // TODO: __INJECT_METHOD
        Drawable drw = this.drawable;
        if (drw != null) {
            stopAnimation(drw);
            drw.clearColorFilter();
            drw.setCallback(null);
        }
        this.drawable = drawable;
        this.isFirstDraw = true;
    }

    public void setOnBoundsChangeListener(Function1<? super Rect, Unit> listener) {
        this.onBoundsChangeListener = listener;
    }

    public int getImageSize() {
        return (int) Utility.dpToPixels(this.type.getSizeDp());
    }

    public boolean isDestroyed() {
        return this.isDestroyed;
    }

    public boolean isReady() {
        return this.drawable != null;
    }

    public void setGrey(boolean state) { // TODO: __INJECT_METHOD
        grey = state;
        isFirstDraw = true;
        Drawable drw = drawable;
        if (drw != null) {
            if (!grey) {
                drw.clearColorFilter();
            } else {
                drw.setColorFilter(GREY_FILTER);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) { // TODO: __REPLACE_METHOD
        Drawable drw = drawable;
        if (drw == null) {
            return;
        }

        if (isFirstDraw) {
            if (grey) {
                drw.setColorFilter(GREY_FILTER);
            } else {
                drw.clearColorFilter();
            }
            isAnimatable = drw instanceof Animatable;
            isFirstDraw = false;
        }

        drw.draw(canvas);
        if (drawAnimation & isAnimatable) {
            startAnimation(drw);
        }

        if (!stack.isEmpty()) {
            for (UrlDrawable stackItem : stack) {
                centerStackItem(stackItem, drw.getBounds());
                stackItem.draw(canvas);
            }
        }
    }

    @Override
    protected void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        Function1<? super Rect, Unit> function1 = this.onBoundsChangeListener;
        if (function1 != null) {
            function1.invoke(rect);
        }
    }

    public void destroy() { // TODO: __REPLACE_METHOD
        if (isDestroyed) {
            return;
        }

        ListIterator<UrlDrawable> iterator = stack.listIterator(stack.size());
        while (iterator.hasPrevious()) {
            iterator.previous().destroy();
        }

        isDestroyed = true;
        Drawable drw = drawable;
        stopAnimation(drw);
        if (drw != null) {
            drw.clearColorFilter();
            drw.setCallback(null);
        }
        drawable = null;
        onBoundsChangeListener = null;
    }

    private static void startAnimation(Drawable drawable) { // TODO: __INJECT_METHOD
        if (drawable instanceof Animatable anim) {
            if (!anim.isRunning()) {
                anim.start();
            }
        }
    }

    private static void stopAnimation(Drawable drawable) { // TODO: __INJECT_METHOD
        if (drawable instanceof Animatable anim) {
            if (anim.isRunning()) {
                anim.stop();
            }
        }
    }

    private void centerStackItem(UrlDrawable stackItem, Rect mainBounds) { // TODO: __INJECT_METHOD
        Drawable stackDrawable = stackItem.drawable;
        if (stackDrawable == null) {
            return;
        }

        Rect rect = stackDrawable.getBounds();
        int top = rect.top;
        int bottom = rect.bottom;

        int stackWidth = stackDrawable.getIntrinsicWidth();

        if (stackWidth <= 0) {
            return;
        }
        if (mainBounds.right > rect.right) {
            return;
        }

        int centerX = mainBounds.centerX();

        int left = centerX - stackWidth / 2;
        int right = left + stackWidth;

        stackDrawable.setBounds(left, top, right, bottom);
    }
}
