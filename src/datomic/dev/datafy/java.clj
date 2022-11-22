;; Copyright (c) Cognitect, Inc.
;; All rights reserved.
;;
;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;;
;;      http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS-IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.

(ns datomic.dev.datafy.java
  (:require [clojure.core.protocols :as p]))

(set! *warn-on-reflection* true)

(defn hexify
  "Convert byte array to hex string"
  ([^bytes bs]
   (hexify bs 0 (alength bs)))
  ([^bytes bs pos len]
   (let [hex [\0 \1 \2 \3 \4 \5 \6 \7 \8 \9 \A \B \C \D \E \F]
         ^chars buf (char-array (* 2 len))]
     (loop [idx pos
            out-idx 0
            ct 0]
       (if (< ct len)
         (let [b (bit-and 0xff (aget bs idx))]
           (aset-char buf out-idx (hex (bit-shift-right b 4)))
           (aset-char buf (inc out-idx) (hex (bit-and b 0x0F)))
           (recur (inc idx) (+ 2 out-idx) (inc ct)))
         (String. buf))))))

(defn- byte-buffer-prefix-array
  ^bytes [^java.nio.ByteBuffer bb n]
  (when (pos? (.remaining bb))
    (let [cursor (.duplicate bb)
          arr (byte-array (min (.remaining cursor) n))]
      (.get cursor arr)
      arr)))

(defn datafy!
  "Datafies Java machinery. Currently mostly I/O."
  []
  (extend-protocol p/Datafiable
    java.net.ServerSocket
    (datafy
      [this]
      {:channel (.getChannel this)
       :inetAddress (.getInetAddress this)
       :localPort (.getLocalPort this)
       :localSocketAddress (.getLocalSocketAddress this)
       :receiveBufferSize (.getReceiveBufferSize this)
       :reuseAddress (.getReuseAddress this)
       :soTimeout (.getSoTimeout this)
       :isBound (.isBound this)
       :isClosed (.isClosed this)})
    java.lang.ThreadGroup
    (datafy
      [this]
      {:activeCount (.activeCount this)
       :maxPriority (.getMaxPriority this)
       :name (.getName this)
       :parent (.getParent this)
       :isDaemon (.isDaemon this)
       :isDestroyed (.isDestroyed this)
       ;; could navify '...' to enumerate all threads in the group?
       })
    java.lang.Thread
    (datafy
      [this]
      {:id (.getId this)
       :name (.getName this)
       :priority (.getPriority this)
       :threadGroup (.getThreadGroup this)
       :isInterrupted (.isInterrupted this)
       :isAlive (.isAlive this)
       :isDaemon (.isDaemon this)})
    java.util.concurrent.ThreadPoolExecutor
    (datafy [this] (bean this))
    java.nio.HeapByteBuffer
    (datafy
      [this]
      (let [arr (byte-buffer-prefix-array this 64)]
        {:position (.position this)
         :remaining (.remaining this)
         :limit (.limit this)
         :capacity (.capacity this)
         :prefix-hex (when arr (hexify arr))
         :prefix-utf8 (when arr (String. arr "UTF-8"))}))))
