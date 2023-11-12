# Image Processor

This Java application processes images by pixelating them. It provides options for single or multi-threaded processing.

### Instructions

1. **Clone Repository:** 
    ```bash
    git clone https://github.com/ADA-GWU/3-concurrency-DjavidHesenov.git
    ```

2. **Run the Application:** 
    ```bash
    cd ./src
    javac ImageProcessor.java
    java ImageProcessor [filename] [squareSize] [mode]
    ```

    - `[filename]` is the image file path.
    - `[squareSize]` specifies the size of the pixel square.
    - Choose `[mode]`:
        - `S` for single-threaded.
        - `M` for multi-threaded processing.

3. **Example:**
    ```bash
    java ImageProcessor myimage.jpg 10 S
    ```

    This processes `myimage.jpg` with a 10x10 pixel square using a single thread.

### Notes

- **Image Size:** Large images are fit to an 80% window without scaling down.
- **Output:** Processed image saved as `result.jpg` in the project directory.
