#include <stdio.h>
#include <stdlib.h>

#define INIT_SIZE 4096

char *readFileToBuffer(const char *path) {
	char *buff;
	FILE *f = fopen(path, "RB");
	if (!f)
		return NULL;

	buff = malloc(sizeof(char)*INIT_SIZE);
	if (!buff) {
		fclose(f);
		return NULL;
	}

	char chr;
	int i = 0;
	unsigned allocated_size = INIT_SIZE;
	while ((chr = fgetc(f)) != EOF) {
		if (allocated_size == i) {
			allocated_size *= 2;
			buff = realloc(buff, allocated_size);
			if (!buff) {
				fclose(f);
				return NULL;
			}
		}

		buff[i++] = chr;
	}

	fclose(f);
	return buff;
}
