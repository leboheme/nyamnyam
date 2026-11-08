# Nyam Nyam

Nyam Nyam is a LibGDX game with desktop and browser targets. `desktop` is the
development launcher. `html` builds a TeaVM runtime and a small Vite shell for
browser testing and distribution. The web canvas keeps a horizontal 640 × 360
aspect ratio and scales to fit its container.

## Requirements

- Java 17 or later as the Gradle runtime. The TeaVM Gradle plugin requires it.
- Node.js 20 or later and npm for the web shell.

## Build

```bash
./gradlew :desktop:run
make compile
make web-dev
make html-build
make html-serve
make html-zip
```

Make targets select Java 17 with macOS `java_home` when it is installed. On
other systems, or to choose a specific installation, set `JAVA17_HOME` before
running Make. Direct Gradle commands require Java 17+ in `JAVA_HOME` or `PATH`.

`make web-dev` prepares the TeaVM runtime and starts Vite on port 5174, next
to Mino's development server on port 5173. It binds to the local network so
the browser build can also be opened from another device.

The static site is written to `html/build/web/`. The ZIP distribution is
written to `html/build/distributions/nyamnyam-html.zip`.

Bitmap fonts are generated from the original screen TTF assets with:

```bash
./gradlew :desktop:generateBitmapFonts
```

The generated `.fnt` and `.png` files are checked in as runtime assets. This
keeps font rendering independent of native FreeType in the browser build.
