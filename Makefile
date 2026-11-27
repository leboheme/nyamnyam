NPM := npm --prefix html
HTML_DIST := html/build/web
HTML_ZIP := html/build/distributions/nyamnyam-html.zip
JAVA17_HOME ?= $(shell /usr/libexec/java_home -v 17 2>/dev/null)
GRADLE := $(if $(JAVA17_HOME),JAVA_HOME="$(JAVA17_HOME)" ,)./gradlew
LAN_IP := $(shell ipconfig getifaddr en0 2>/dev/null || ipconfig getifaddr en1 2>/dev/null || hostname -I 2>/dev/null | awk '{print $$1}')

.PHONY: compile web-runtime web-dev html-build html-serve html-zip promote-stages compile-room compile-world

compile:
	$(GRADLE) :core:compileJava :desktop:compileJava :html:compileJava

web-runtime:
	$(GRADLE) :html:prepareWebRuntime

web-dev: web-runtime html/node_modules/.package-lock.json
	@echo "Local: http://localhost:5174"
	@if [ -n "$(LAN_IP)" ]; then echo "Red local: http://$(LAN_IP):5174"; else echo "Red local: no se pudo detectar la IP"; fi
	$(NPM) run dev -- --host 0.0.0.0 --port 5174 --strictPort

html/node_modules/.package-lock.json: html/package-lock.json
	$(NPM) ci

html-build: web-runtime html/node_modules/.package-lock.json
	$(NPM) run build

html-serve: html-build
	$(NPM) run preview -- --host 127.0.0.1 --port 8000

html-zip: html-build
	mkdir -p html/build/distributions
	cd $(HTML_DIST) && zip -r -q -FS ../distributions/nyamnyam-html.zip .

promote-stages:
	@if [ -z "$(STAGES)" ]; then echo 'Usage: make promote-stages STAGES="3 4 5"'; exit 2; fi
	python3 tools/promote_stages.py $(STAGES)

compile-room:
	@if [ -z "$(ROOM)" ]; then echo 'Usage: make compile-room ROOM=7'; exit 2; fi
	python3 tools/compile_stage.py $(ROOM) $(if $(FORCE),--force,)

compile-world:
	@if [ -z "$(WORLD)" ]; then echo 'Usage: make compile-world WORLD=world_01'; exit 2; fi
	python3 tools/compile_world.py $(WORLD) $(if $(FORCE),--force,)
