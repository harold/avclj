(ns avclj-test
  (:require [clojure.test :refer [deftest is]]
            [avclj :as avclj]
            [tech.v3.tensor :as dtt]
            [tech.v3.datatype :as dtype]
            [tech.v3.libs.buffered-image :as bufimg]))


(defn img-tensor
  [shape ^long offset]
  (dtt/compute-tensor shape
                      (fn [^long y ^long x ^long c]
                        (let [ymod (-> (quot (+ y offset) 32)
                                       (mod 2))
                              xmod (-> (quot (+ x offset) 32)
                                       (mod 2))]
                          (if (and (== 0 xmod)
                                   (== 0 ymod))
                            255
                            0)))
                      :uint8))


(defn save-tensor
  [tens fname]
  (let [[h w c] (dtype/shape tens)
        bufimg (bufimg/new-image h w :byte-bgr)]
    (-> (dtype/copy! tens bufimg)
        (bufimg/save! fname))))


(defn encode-demo
  ([{:keys [encoder-name
            output-fname]
     :or {encoder-name "mpeg4"
          output-fname "test-video.mpeg4"}}]
   (avclj/initialize!)
   (with-open [encoder (avclj/make-video-encoder 256 256 output-fname
                                                 {:encoder-name encoder-name})]
     (dotimes [iter 125]
       (avclj/encode-frame! encoder (img-tensor [256 256 3] iter)))))
  ([] (encode-demo nil)))
