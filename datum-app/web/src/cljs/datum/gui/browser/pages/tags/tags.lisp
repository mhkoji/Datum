(ns datum.gui.browser.pages.tags
  (:require [reagent.core :as r]
            [datum.tag.api]
            [datum.tag.edit-tags :as edit-tags]
            [datum.tag.edit-content-tags :as edit-content-tags]
            [datum.gui.browser.components.header.state :as header]
            [datum.gui.browser.pages.tags.components]
            [datum.gui.browser.url :as url]
            [datum.gui.browser.util :as util]))

(defn create-store [update-store]
  (let [et-transaction
        (reify edit-tags/Transaction
          (edit-tags/update-state [_ f]
            (update-store #(update-in % [:edit-tags :state] f))))

        et-api
        (reify edit-tags/Transaction
          (edit-tags/get-all-tags [_ k]))

        ect-transaction
        (reify edit-content-tags/Transaction
          (edit-content-tags/update-state [_ f]
            (update-store #(update-in % [:edit-tags :state] f))))

        ect-api
        (reify edit-content-tags/Transaction
          (edit-content-tags/get-all-tags [_ k]))]
    {:edit-tags
     {:state
      (edit-tags/State. nil nil)

      :load-tags
      #(edit-tags/refresh-tags et-transaction et-api)
      :submit
      #(edit-tags/submit       et-transaction et-api nil)
      :set-name
      #(edit-tags/set-name     et-transaction %)
      :delete
      #(edit-tags/delete       et-transaction et-api % nil)
      }

     :edit-content-tags
     {:state
      (edit-content-tags/State. nil nil)

      :start
      #(edit-content-tags/start ect-transaction ect-api)
      :submit
      #(edit-content-tags/submit ect-transaction ect-api)
      :attach-tag
      #(edit-content-tags/attach-tag ect-transaction %)
      :detach-tag
      #(edit-content-tags/detach-tag ect-transaction %)
      }
     }))


(defn create-renderer [elem offset count]
  (fn [store]
    (r/render [datum.gui.browser.pages.tags.components/page
               {:header
                (header/get-state :tag)

                :pager
                {:prev (if (<= count offset)
                         {:link (url/tags (- offset count) count)
                          :enabled true}
                         {:link ""
                          :enabled false})
                 :next {:link (url/tags (+ offset count) count)
                        :enabled true}}

                :show-covers
                (let [{:keys [state execute]} (-> store :show-covers)]
                  {:state state
                   :execute #(execute offset count)})
                }]
              elem)))


(defn render-loop [elem _]
  (util/render-loop {:create-store create-store
                       :render (create-renderer elem offset count)})))
