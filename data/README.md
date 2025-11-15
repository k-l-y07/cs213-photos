Place stock photos here for the "stock" user.

Create a folder named `stock` inside this `data` directory and add 5-10 low/medium-resolution images (png/jpg/gif/bmp).

Example:

  data/stock/photo1.jpg
  data/stock/photo2.jpg

When the application is run, the `AppState` loader looks for the `data/users.dat` file. If that file is missing, it will ensure a `stock` user and an empty `stock` album exist. To preload stock photos into the `stock` album, you may place images in `data/stock` and then update the application (or import them via the UI).
