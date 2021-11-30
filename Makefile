DAY=$$(date +"%d")

.PHONY: kotlin
kotlin:
	echo $(DAY)
	cp -R tmp $(DAY)
	mv $(DAY)/tmp.iml $(DAY)/aoc$(DAY).iml
	sed -i "s/tmp/aoc$(DAY)/g" ./$(DAY)/.idea/modules.xml

