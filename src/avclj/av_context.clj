(ns avclj.av-context
  (:require [tech.v3.datatype.ffi :as dt-ffi]
            [tech.v3.datatype.ffi.size-t :as ffi-size-t]
            [tech.v3.datatype.ffi.clang :as ffi-clang]
            [tech.v3.datatype.struct :as dt-struct]
            [tech.v3.datatype.errors :as errors]
            [camel-snake-kebab.core :as csk]
            [clojure.string :as s])
  (:import [java.util Map]))

(def ^{:doc "Record layout produced using clang -fdump-record-layout option during compilation"}
  context-layout
  "      0 |   const AVClass * av_class
         8 |   int log_level_offset
        12 |   enum AVMediaType codec_type
        16 |   const struct AVCodec * codec
        24 |   enum AVCodecID codec_id
        28 |   unsigned int codec_tag
        32 |   void * priv_data
        40 |   struct AVCodecInternal * internal
        48 |   void * opaque
        56 |   int64_t bit_rate
        64 |   int bit_rate_tolerance
        68 |   int global_quality
        72 |   int compression_level
        76 |   int flags
        80 |   int flags2
        88 |   uint8_t * extradata
        96 |   int extradata_size
       100 |   struct AVRational time_base
       100 |     int num
       104 |     int den
       108 |   int ticks_per_frame
       112 |   int delay
       116 |   int width
       120 |   int height
       124 |   int coded_width
       128 |   int coded_height
       132 |   int gop_size
       136 |   enum AVPixelFormat pix_fmt
       144 |   void (*)(struct AVCodecContext *, const AVFrame *, int *, int, int, int) draw_horiz_band
       152 |   enum AVPixelFormat (*)(struct AVCodecContext *, const enum AVPixelFormat *) get_format
       160 |   int max_b_frames
       164 |   float b_quant_factor
       168 |   int b_frame_strategy
       172 |   float b_quant_offset
       176 |   int has_b_frames
       180 |   int mpeg_quant
       184 |   float i_quant_factor
       188 |   float i_quant_offset
       192 |   float lumi_masking
       196 |   float temporal_cplx_masking
       200 |   float spatial_cplx_masking
       204 |   float p_masking
       208 |   float dark_masking
       212 |   int slice_count
       216 |   int prediction_method
       224 |   int * slice_offset
       232 |   struct AVRational sample_aspect_ratio
       232 |     int num
       236 |     int den
       240 |   int me_cmp
       244 |   int me_sub_cmp
       248 |   int mb_cmp
       252 |   int ildct_cmp
       256 |   int dia_size
       260 |   int last_predictor_count
       264 |   int pre_me
       268 |   int me_pre_cmp
       272 |   int pre_dia_size
       276 |   int me_subpel_quality
       280 |   int me_range
       284 |   int slice_flags
       288 |   int mb_decision
       296 |   uint16_t * intra_matrix
       304 |   uint16_t * inter_matrix
       312 |   int scenechange_threshold
       316 |   int noise_reduction
       320 |   int intra_dc_precision
       324 |   int skip_top
       328 |   int skip_bottom
       332 |   int mb_lmin
       336 |   int mb_lmax
       340 |   int me_penalty_compensation
       344 |   int bidir_refine
       348 |   int brd_scale
       352 |   int keyint_min
       356 |   int refs
       360 |   int chromaoffset
       364 |   int mv0_threshold
       368 |   int b_sensitivity
       372 |   enum AVColorPrimaries color_primaries
       376 |   enum AVColorTransferCharacteristic color_trc
       380 |   enum AVColorSpace colorspace
       384 |   enum AVColorRange color_range
       388 |   enum AVChromaLocation chroma_sample_location
       392 |   int slices
       396 |   enum AVFieldOrder field_order
       400 |   int sample_rate
       404 |   int channels
       408 |   enum AVSampleFormat sample_fmt
       412 |   int frame_size
       416 |   int frame_number
       420 |   int block_align
       424 |   int cutoff
       432 |   uint64_t channel_layout
       440 |   uint64_t request_channel_layout
       448 |   enum AVAudioServiceType audio_service_type
       452 |   enum AVSampleFormat request_sample_fmt
       456 |   int (*)(struct AVCodecContext *, AVFrame *, int) get_buffer2
       464 |   int refcounted_frames
       468 |   float qcompress
       472 |   float qblur
       476 |   int qmin
       480 |   int qmax
       484 |   int max_qdiff
       488 |   int rc_buffer_size
       492 |   int rc_override_count
       496 |   RcOverride * rc_override
       504 |   int64_t rc_max_rate
       512 |   int64_t rc_min_rate
       520 |   float rc_max_available_vbv_use
       524 |   float rc_min_vbv_overflow_use
       528 |   int rc_initial_buffer_occupancy
       532 |   int coder_type
       536 |   int context_model
       540 |   int frame_skip_threshold
       544 |   int frame_skip_factor
       548 |   int frame_skip_exp
       552 |   int frame_skip_cmp
       556 |   int trellis
       560 |   int min_prediction_order
       564 |   int max_prediction_order
       568 |   int64_t timecode_frame_start
       576 |   void (*)(struct AVCodecContext *, void *, int, int) rtp_callback
       584 |   int rtp_payload_size
       588 |   int mv_bits
       592 |   int header_bits
       596 |   int i_tex_bits
       600 |   int p_tex_bits
       604 |   int i_count
       608 |   int p_count
       612 |   int skip_count
       616 |   int misc_bits
       620 |   int frame_bits
       624 |   char * stats_out
       632 |   char * stats_in
       640 |   int workaround_bugs
       644 |   int strict_std_compliance
       648 |   int error_concealment
       652 |   int debug
       656 |   int err_recognition
       664 |   int64_t reordered_opaque
       672 |   const struct AVHWAccel * hwaccel
       680 |   void * hwaccel_context
       688 |   uint64_t [8] error
       752 |   int dct_algo
       756 |   int idct_algo
       760 |   int bits_per_coded_sample
       764 |   int bits_per_raw_sample
       768 |   int lowres
       776 |   AVFrame * coded_frame
       784 |   int thread_count
       788 |   int thread_type
       792 |   int active_thread_type
       796 |   int thread_safe_callbacks
       800 |   int (*)(struct AVCodecContext *, int (*)(struct AVCodecContext *, void *), void *, int *, int, int) execute
       808 |   int (*)(struct AVCodecContext *, int (*)(struct AVCodecContext *, void *, int, int), void *, int *, int) execute2
       816 |   int nsse_weight
       820 |   int profile
       824 |   int level
       828 |   enum AVDiscard skip_loop_filter
       832 |   enum AVDiscard skip_idct
       836 |   enum AVDiscard skip_frame
       840 |   uint8_t * subtitle_header
       848 |   int subtitle_header_size
       856 |   uint64_t vbv_delay
       864 |   int side_data_only_packets
       868 |   int initial_padding
       872 |   struct AVRational framerate
       872 |     int num
       876 |     int den
       880 |   enum AVPixelFormat sw_pix_fmt
       884 |   struct AVRational pkt_timebase
       884 |     int num
       888 |     int den
       896 |   const AVCodecDescriptor * codec_descriptor
       904 |   int64_t pts_correction_num_faulty_pts
       912 |   int64_t pts_correction_num_faulty_dts
       920 |   int64_t pts_correction_last_pts
       928 |   int64_t pts_correction_last_dts
       936 |   char * sub_charenc
       944 |   int sub_charenc_mode
       948 |   int skip_alpha
       952 |   int seek_preroll
       956 |   int debug_mv
       960 |   uint16_t * chroma_intra_matrix
       968 |   uint8_t * dump_separator
       976 |   char * codec_whitelist
       984 |   unsigned int properties
       992 |   AVPacketSideData * coded_side_data
      1000 |   int nb_coded_side_data
      1008 |   AVBufferRef * hw_frames_ctx
      1016 |   int sub_text_format
      1020 |   int trailing_padding
      1024 |   int64_t max_pixels
      1032 |   AVBufferRef * hw_device_ctx
      1040 |   int hwaccel_flags
      1044 |   int apply_cropping
      1048 |   int extra_hw_frames
      1052 |   int discard_damaged_percentage")


(def frame-layout
  "      0 |   uint8_t *[8] data
        64 |   int [8] linesize
        96 |   uint8_t ** extended_data
       104 |   int width
       108 |   int height
       112 |   int nb_samples
       116 |   int format
       120 |   int key_frame
       124 |   enum AVPictureType pict_type
       128 |   struct AVRational sample_aspect_ratio
       128 |     int num
       132 |     int den
       136 |   int64_t pts
       144 |   int64_t pkt_pts
       152 |   int64_t pkt_dts
       160 |   int coded_picture_number
       164 |   int display_picture_number
       168 |   int quality
       176 |   void * opaque
       184 |   uint64_t [8] error
       248 |   int repeat_pict
       252 |   int interlaced_frame
       256 |   int top_field_first
       260 |   int palette_has_changed
       264 |   int64_t reordered_opaque
       272 |   int sample_rate
       280 |   uint64_t channel_layout
       288 |   AVBufferRef *[8] buf
       352 |   AVBufferRef ** extended_buf
       360 |   int nb_extended_buf
       368 |   AVFrameSideData ** side_data
       376 |   int nb_side_data
       380 |   int flags
       384 |   enum AVColorRange color_range
       388 |   enum AVColorPrimaries color_primaries
       392 |   enum AVColorTransferCharacteristic color_trc
       396 |   enum AVColorSpace colorspace
       400 |   enum AVChromaLocation chroma_location
       408 |   int64_t best_effort_timestamp
       416 |   int64_t pkt_pos
       424 |   int64_t pkt_duration
       432 |   AVDictionary * metadata
       440 |   int decode_error_flags
       444 |   int channels
       448 |   int pkt_size
       456 |   int8_t * qscale_table
       464 |   int qstride
       468 |   int qscale_type
       472 |   AVBufferRef * qp_table_buf
       480 |   AVBufferRef * hw_frames_ctx
       488 |   AVBufferRef * opaque_ref
       496 |   size_t crop_top
       504 |   size_t crop_bottom
       512 |   size_t crop_left
       520 |   size_t crop_right
       528 |   AVBufferRef * private_ref")


(def packet-layout
  "      0 |   AVBufferRef * buf
         8 |   int64_t pts
        16 |   int64_t dts
        24 |   uint8_t * data
        32 |   int size
        36 |   int stream_index
        40 |   int flags
        48 |   AVPacketSideData * side_data
        56 |   int side_data_elems
        64 |   int64_t duration
        72 |   int64_t pos
        80 |   int64_t convergence_duration")

(def codec-layout
  "      0 |   const char * name
         8 |   const char * long_name
        16 |   enum AVMediaType type
        20 |   enum AVCodecID id
        24 |   int capabilities
        32 |   const AVRational * supported_framerates
        40 |   const enum AVPixelFormat * pix_fmts
        48 |   const int * supported_samplerates
        56 |   const enum AVSampleFormat * sample_fmts
        64 |   const uint64_t * channel_layouts
        72 |   uint8_t max_lowres
        80 |   const AVClass * priv_class
        88 |   const AVProfile * profiles
        96 |   const char * wrapper_name
       104 |   int priv_data_size
       112 |   struct AVCodec * next
       120 |   int (*)(AVCodecContext *) init_thread_copy
       128 |   int (*)(AVCodecContext *, const AVCodecContext *) update_thread_context
       136 |   const AVCodecDefault * defaults
       144 |   void (*)(struct AVCodec *) init_static_data
       152 |   int (*)(AVCodecContext *) init
       160 |   int (*)(AVCodecContext *, uint8_t *, int, const struct AVSubtitle *) encode_sub
       168 |   int (*)(AVCodecContext *, AVPacket *, const AVFrame *, int *) encode2
       176 |   int (*)(AVCodecContext *, void *, int *, AVPacket *) decode
       184 |   int (*)(AVCodecContext *) close
       192 |   int (*)(AVCodecContext *, const AVFrame *) send_frame
       200 |   int (*)(AVCodecContext *, AVPacket *) receive_packet
       208 |   int (*)(AVCodecContext *, AVFrame *) receive_frame
       216 |   void (*)(AVCodecContext *) flush
       224 |   int caps_internal
       232 |   const char * bsfs
       240 |   const struct AVCodecHWConfigInternal ** hw_configs")


(dt-struct/define-datatype! :av-rational [{:name :num :datatype :int32}
                                          {:name :den :datatype :int32}])


(def context-def* (delay (ffi-clang/defstruct-from-layout
                           :av-context context-layout)))

(def packet-def* (delay (ffi-clang/defstruct-from-layout
                          :av-packet packet-layout)))

(def frame-def* (delay (ffi-clang/defstruct-from-layout
                         :av-frame frame-layout)))

(def codec-def* (delay (ffi-clang/defstruct-from-layout
                         :av-codec codec-layout)))
