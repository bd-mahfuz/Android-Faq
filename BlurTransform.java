import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.squareup.picasso.Transformation;

public class BlurTransform implements Transformation {

    static BlurTransform blurTransform;
    RenderScript rs;

    protected BlurTransform() {
        // Exists only to defeat instantiation.
    }

    private BlurTransform(Context context) {
        super();
        rs = RenderScript.create(context);
    }

    public static BlurTransform getInstance(Context context) {
        if (blurTransform == null) {
            blurTransform = new BlurTransform(context);
        }
        return blurTransform;
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {

        Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        for (int i = 0; i < 10; i++){
            final Allocation input = Allocation.createFromBitmap(rs, outputBitmap); //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            script.setRadius(25F);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(outputBitmap);
        }

        bitmap.recycle();

        return outputBitmap;
    }

    @Override
    public String key() {
        return "blur";
    }
