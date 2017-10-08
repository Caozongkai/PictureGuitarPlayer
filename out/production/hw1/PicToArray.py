from PIL import Image
from math import *
import sys, os

TIME = 15
NOTE_PER_SEC = 4
CHUCK = TIME * NOTE_PER_SEC

def picToArray(filename):
    im = Image.open(filename)
    im = im.convert("L")
    width, height = im.size
    block_width = int(sqrt(width*height/CHUCK))
    X = int(width / block_width)
    Y = int(height / block_width)
    arr = getPicChuckValues(im, X, Y, block_width)
    return arr, X, Y, block_width

def toJava(filename):
    arr, x, y, block_width = picToArray(filename)
    reconstructImage(arr, x, y, block_width).show()
    return arr

def getPicChuckValues(image, X, Y, block_width):
    arr = []
    for i in range(Y):
        for j in range(X):
            y_offset = i * block_width
            x_offset = j * block_width
            block_average = getBlockAvg(image, x_offset, y_offset, block_width)
            arr.append(block_average)
    return arr

def getBlockAvg(image, x_offset, y_offset, block_width):
    total = 0
    for i in range(block_width):
        for j in range(block_width):
            total += image.getpixel((x_offset + j, y_offset + i))
    return float(total) / (block_width * block_width)

def reconstructImage(gray_scales_array, X, Y, block_width):
    def recover_block(image, x_offset, y_offset, block_width, value):
        for i in range(block_width):
            for j in range(block_width):
                image.putpixel((x_offset + j, y_offset + i), int(value))
    im = Image.new("L", (X * block_width, Y * block_width))
    for i in range(Y):
        for j in range(X):
            value = gray_scales_array[i * X + j]
            y_offset = i * block_width
            x_offset = j * block_width
            recover_block(im, x_offset, y_offset, block_width, value)
    return im

def main():
    filename = sys.argv[1]
    try:
        os.remove("temp.txt")
    except OSError:
        pass
    file = open("temp.txt", "w")
    arr = toJava(filename)
    for i in arr:
        val = round(i / 255 * 30)
        file.write(str(val) + "\n")

if __name__ == "__main__":
    main()