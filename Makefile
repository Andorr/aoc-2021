DAY=$$(date +"%d")
DAY_SHORT=$$(echo $(DAY) | sed 's/^0*//')
TOKEN=$$(cat cookie)

.PHONY: kotlin
kotlin:
	echo $(DAY)
	cp -R tmp $(DAY)
	mv $(DAY)/tmp.iml $(DAY)/aoc$(DAY).iml
	sed -i "s/tmp/aoc$(DAY)/g" ./$(DAY)/.idea/modules.xml
	curl --cookie session=$(TOKEN) https://adventofcode.com/2021/day/$(DAY_SHORT)/input > $(DAY)/input.txt


.PHONY: token
token:
	echo $(TOKEN)
