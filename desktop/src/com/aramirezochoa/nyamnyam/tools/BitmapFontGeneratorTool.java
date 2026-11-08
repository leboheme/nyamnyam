package com.aramirezochoa.nyamnyam.tools;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.tools.bmfont.BitmapFontWriter;
import com.badlogic.gdx.utils.Array;

import java.io.File;

public final class BitmapFontGeneratorTool {
    private BitmapFontGeneratorTool() {
    }

    public static void main(String[] args) {
        Lwjgl3NativesLoader.load();
        FileHandle assets = new FileHandle(new File(args[0]));
        generate(assets, "data/menu/font.ttf", new int[]{48, 40});
        generate(assets, "data/game/font.ttf", new int[]{24, 14, 52});
        generate(assets, "data/intro/font.ttf", new int[]{48});
        generate(assets, "data/end/font.ttf", new int[]{80});
        generate(assets, "data/help/font.ttf", new int[]{46});
        generate(assets, "data/store/font.ttf", new int[]{46, 30});
    }

    private static void generate(FileHandle assets, String source, int[] sizes) {
        for (int size : sizes) {
            FileHandle target = assets.child(source.replace(".ttf", "-" + size + ".fnt"));
            target.parent().mkdirs();
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(assets.child(source));
            FreeTypeFontParameter parameter = new FreeTypeFontParameter();
            parameter.size = size;
            parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "€’";
            PixmapPacker packer = new PixmapPacker(1024, 1024, Pixmap.Format.RGBA8888, 2, false);
            parameter.packer = packer;
            FreeTypeBitmapFontData data = generator.generateData(parameter);
            BitmapFontWriter.FontInfo fontInfo = new BitmapFontWriter.FontInfo(source, size);
            fontInfo.overrideMetrics(data);
            Array<PixmapPacker.Page> pages = packer.getPages();
            Pixmap[] pixmaps = new Pixmap[pages.size];
            for (int index = 0; index < pages.size; index++) {
                pixmaps[index] = pages.get(index).getPixmap();
            }
            String[] pageRefs = BitmapFontWriter.writePixmaps(pixmaps, target.parent(), target.nameWithoutExtension());
            BitmapFontWriter.writeFont(data, pageRefs, target, fontInfo, packer.getPageWidth(), packer.getPageHeight());
            data.dispose();
            generator.dispose();
            packer.dispose();
        }
    }
}
