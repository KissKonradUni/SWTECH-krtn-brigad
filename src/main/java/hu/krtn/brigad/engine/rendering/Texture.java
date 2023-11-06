package hu.krtn.brigad.engine.rendering;

import hu.krtn.brigad.engine.window.Logger;
import org.joml.Vector4f;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33.*;

public class Texture {

    private String path;

    private int textureHandle;
    private final int[] slots = new int[] {0, 0, 0, 0, 0, 0, 0, 0};

    public static class ByteColor {
        byte r, g, b, a;

        public ByteColor(byte r, byte g, byte b, byte a) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }

        public ByteColor(Vector4f color) {
            this.r = (byte) Math.max(0, Math.min(255, color.x * 255));
            this.g = (byte) Math.max(0, Math.min(255, color.y * 255));
            this.b = (byte) Math.max(0, Math.min(255, color.z * 255));
            this.a = (byte) Math.max(0, Math.min(255, color.w * 255));
        }
    }

    public Texture(ByteColor color) {
        ByteBuffer data = ByteBuffer.allocateDirect(4);
        data.put(color.r);
        data.put(color.g);
        data.put(color.b);
        data.put(color.a);
        data.flip();

        initTexture(data, 1, 1);

        this.path = null;
    }

    public Texture(ByteBuffer data, int width, int height, String path) {
        if (data == null) {
            textureHandle = 0;
            Logger.error("Texture data is null!");
            return;
        }

        initTexture(data, width, height);
        this.path = path;
    }

    private void initTexture(ByteBuffer data, int width, int height) {
        textureHandle = glGenTextures();

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureHandle);

        //TODO: Make these configurable
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public void bind(int slot) {
        if (slots[slot] == 0)
            glActiveTexture(GL_TEXTURE0 + slot);

        slots[slot] = textureHandle;
        glBindTexture(GL_TEXTURE_2D, textureHandle);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);

        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == textureHandle) {
                slots[i] = 0;
                break;
            }
        }
    }

    public void destroy() {
        glDeleteTextures(textureHandle);
    }

    public String getPath() {
        return path;
    }

}
