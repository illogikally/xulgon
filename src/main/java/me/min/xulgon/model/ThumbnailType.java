package me.min.xulgon.model;

public enum ThumbnailType {
   s40x40(40), s160x160(160), s200x200(200),
   s400x400(400), s600x600(600), s900x900(900);

   private final int size;

   ThumbnailType(int size) {
      this.size = size;
   }

   public int getSize() {
      return this.size;
   }
}
