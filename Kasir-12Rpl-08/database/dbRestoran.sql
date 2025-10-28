--
-- PostgreSQL database dump
--

\restrict P3bmmyLThrFEabxBRe5i3xTumXtKn8eG9XO4apdzv9b1JG18cLUNqFx6gNLO7UP

-- Dumped from database version 13.22
-- Dumped by pg_dump version 13.22

-- Started on 2025-10-28 17:48:18

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 200 (class 1259 OID 24672)
-- Name: agen; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.agen (
    idagen integer NOT NULL,
    namaagen character varying(50)
);


ALTER TABLE public.agen OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 24863)
-- Name: bahan; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bahan (
    idbahan integer NOT NULL,
    namabahan character varying(100) NOT NULL,
    stok numeric(10,2) DEFAULT 0,
    satuan character varying(20),
    hargabeli integer
);


ALTER TABLE public.bahan OWNER TO postgres;

--
-- TOC entry 213 (class 1259 OID 24861)
-- Name: bahan_idbahan_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.bahan_idbahan_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.bahan_idbahan_seq OWNER TO postgres;

--
-- TOC entry 3125 (class 0 OID 0)
-- Dependencies: 213
-- Name: bahan_idbahan_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.bahan_idbahan_seq OWNED BY public.bahan.idbahan;


--
-- TOC entry 220 (class 1259 OID 24900)
-- Name: detail_pembelian; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.detail_pembelian (
    iddetail integer NOT NULL,
    idpembelian integer,
    idbahan integer,
    jumlah numeric(10,2),
    harga_satuan integer,
    satuan character varying(200),
    subtotal integer
);


ALTER TABLE public.detail_pembelian OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 24898)
-- Name: detail_pembelian_iddetail_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.detail_pembelian_iddetail_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.detail_pembelian_iddetail_seq OWNER TO postgres;

--
-- TOC entry 3126 (class 0 OID 0)
-- Dependencies: 219
-- Name: detail_pembelian_iddetail_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.detail_pembelian_iddetail_seq OWNED BY public.detail_pembelian.iddetail;


--
-- TOC entry 201 (class 1259 OID 24675)
-- Name: inventory; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.inventory (
    "idBahan" integer NOT NULL,
    "namaBahan" character varying(100),
    "hargaBahan" integer,
    quantity integer,
    "kategoriBahan" character varying(100)
);


ALTER TABLE public.inventory OWNER TO postgres;

--
-- TOC entry 202 (class 1259 OID 24678)
-- Name: kategori; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.kategori (
    idkategori integer NOT NULL,
    namakategori character varying(50)
);


ALTER TABLE public.kategori OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 24967)
-- Name: kategori_idkategori_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.kategori ALTER COLUMN idkategori ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.kategori_idkategori_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 203 (class 1259 OID 24681)
-- Name: member; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.member (
    idmember integer NOT NULL,
    namamember character varying(50),
    alamat character varying(100),
    notelp character varying(100)
);


ALTER TABLE public.member OWNER TO postgres;

--
-- TOC entry 204 (class 1259 OID 24684)
-- Name: menu; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.menu (
    idmenu integer NOT NULL,
    namamenu character varying(100),
    hargamenu integer,
    stok integer,
    kategori character varying(50),
    gambar character varying(255)
);


ALTER TABLE public.menu OWNER TO postgres;

--
-- TOC entry 205 (class 1259 OID 24687)
-- Name: menu_idmenu_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.menu ALTER COLUMN idmenu ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.menu_idmenu_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 206 (class 1259 OID 24689)
-- Name: metodepembayaran; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.metodepembayaran (
    idmetode integer NOT NULL,
    namametode character varying(50)
);


ALTER TABLE public.metodepembayaran OWNER TO postgres;

--
-- TOC entry 207 (class 1259 OID 24692)
-- Name: pelayan; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pelayan (
    idpelayan integer NOT NULL,
    namapelayan character varying(50),
    pw character varying(50),
    alamat character varying(250),
    role character varying(50),
    telp character varying(50),
    created_at date,
    created_by integer,
    status character varying(20),
    username character varying(200)
);


ALTER TABLE public.pelayan OWNER TO postgres;

--
-- TOC entry 208 (class 1259 OID 24698)
-- Name: pelayan_idpelayan_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.pelayan ALTER COLUMN idpelayan ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.pelayan_idpelayan_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 218 (class 1259 OID 24890)
-- Name: pembelian_bahan; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pembelian_bahan (
    idpembelian integer NOT NULL,
    tanggal timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    total integer DEFAULT 0
);


ALTER TABLE public.pembelian_bahan OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 24888)
-- Name: pembelian_bahan_idpembelian_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pembelian_bahan_idpembelian_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.pembelian_bahan_idpembelian_seq OWNER TO postgres;

--
-- TOC entry 3127 (class 0 OID 0)
-- Dependencies: 217
-- Name: pembelian_bahan_idpembelian_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.pembelian_bahan_idpembelian_seq OWNED BY public.pembelian_bahan.idpembelian;


--
-- TOC entry 216 (class 1259 OID 24872)
-- Name: resep; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.resep (
    idresep integer NOT NULL,
    idmenu integer,
    idbahan integer,
    jumlah_bahan numeric(10,2),
    satuan character varying(20)
);


ALTER TABLE public.resep OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 24870)
-- Name: resep_idresep_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.resep_idresep_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.resep_idresep_seq OWNER TO postgres;

--
-- TOC entry 3128 (class 0 OID 0)
-- Dependencies: 215
-- Name: resep_idresep_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.resep_idresep_seq OWNED BY public.resep.idresep;


--
-- TOC entry 210 (class 1259 OID 24762)
-- Name: transaksi; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.transaksi (
    id integer NOT NULL,
    no_transaksi character varying(20) NOT NULL,
    member character varying(50),
    nama character varying(100),
    agen character varying(50),
    pelayan character varying(50),
    tgl_transaksi date NOT NULL,
    keterangan text,
    subtotal numeric(12,2) DEFAULT 0,
    diskon numeric(5,2) DEFAULT 0,
    pajak numeric(5,2) DEFAULT 0,
    service numeric(5,2) DEFAULT 0,
    grand_total numeric(12,2) DEFAULT 0,
    tunai numeric(12,2) DEFAULT 0,
    kredit numeric(12,2) DEFAULT 0,
    kembalian numeric(12,2) DEFAULT 0,
    akun_kas character varying(50) DEFAULT 'Uang di Tangan'::character varying
);


ALTER TABLE public.transaksi OWNER TO postgres;

--
-- TOC entry 212 (class 1259 OID 24784)
-- Name: transaksi_detail; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.transaksi_detail (
    id integer NOT NULL,
    id_transaksi integer,
    kode_menu character varying(20),
    nama_menu character varying(100),
    qty integer,
    harga numeric(12,2),
    jumlah numeric(12,2),
    status character varying(20),
    keterangan text
);


ALTER TABLE public.transaksi_detail OWNER TO postgres;

--
-- TOC entry 211 (class 1259 OID 24782)
-- Name: transaksi_detail_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.transaksi_detail_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.transaksi_detail_id_seq OWNER TO postgres;

--
-- TOC entry 3129 (class 0 OID 0)
-- Dependencies: 211
-- Name: transaksi_detail_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.transaksi_detail_id_seq OWNED BY public.transaksi_detail.id;


--
-- TOC entry 209 (class 1259 OID 24760)
-- Name: transaksi_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.transaksi_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.transaksi_id_seq OWNER TO postgres;

--
-- TOC entry 3130 (class 0 OID 0)
-- Dependencies: 209
-- Name: transaksi_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.transaksi_id_seq OWNED BY public.transaksi.id;


--
-- TOC entry 2928 (class 2604 OID 24866)
-- Name: bahan idbahan; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bahan ALTER COLUMN idbahan SET DEFAULT nextval('public.bahan_idbahan_seq'::regclass);


--
-- TOC entry 2934 (class 2604 OID 24903)
-- Name: detail_pembelian iddetail; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.detail_pembelian ALTER COLUMN iddetail SET DEFAULT nextval('public.detail_pembelian_iddetail_seq'::regclass);


--
-- TOC entry 2931 (class 2604 OID 24893)
-- Name: pembelian_bahan idpembelian; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pembelian_bahan ALTER COLUMN idpembelian SET DEFAULT nextval('public.pembelian_bahan_idpembelian_seq'::regclass);


--
-- TOC entry 2930 (class 2604 OID 24875)
-- Name: resep idresep; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resep ALTER COLUMN idresep SET DEFAULT nextval('public.resep_idresep_seq'::regclass);


--
-- TOC entry 2917 (class 2604 OID 24765)
-- Name: transaksi id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transaksi ALTER COLUMN id SET DEFAULT nextval('public.transaksi_id_seq'::regclass);


--
-- TOC entry 2927 (class 2604 OID 24787)
-- Name: transaksi_detail id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transaksi_detail ALTER COLUMN id SET DEFAULT nextval('public.transaksi_detail_id_seq'::regclass);


--
-- TOC entry 3098 (class 0 OID 24672)
-- Dependencies: 200
-- Data for Name: agen; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.agen (idagen, namaagen) FROM stdin;
1	-
2	Go Food
3	Shopee Food
4	Maxim
5	Lainnya
\.


--
-- TOC entry 3112 (class 0 OID 24863)
-- Dependencies: 214
-- Data for Name: bahan; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.bahan (idbahan, namabahan, stok, satuan, hargabeli) FROM stdin;
23	Susu	0.00	Bungkus	2000
24	Es Batu	0.00	Kg	10000
1	Beras	522.00	kg	14000
16	Daging Sapi	500.00	kg	120000
20	Wortel	500.00	kg	10000
17	Tahu	500.00	kg	1000
18	Tempe	500.00	kg	1000
10	Kopi Bubuk	1000.00	bungkus	8000
12	Saus Sambal	500.00	liter	10000
21	Bawang Bombay	1000.00	kg	80000
22	Kentang	1000.00	kg	10000
2	Telur Ayam	480.00	butir	2500
9	Teh Celup	970.00	pcs	1000
14	Air Mineral	92.50	liter	4000
6	Cabai Merah	496.00	kg	40000
8	Gula Pasir	499.60	kg	15000
19	Sayur Kol	492.00	kg	8000
7	Mie	960.00	bungkus	2500
11	Kecap Manis	496.00	liter	12000
15	Ayam	483.60	kg	35000
13	Garam	498.00	kg	7000
4	Bawang Merah	491.00	kg	30000
5	Bawang Putih	489.60	kg	28000
3	Minyak Goreng	488.00	liter	18000
\.


--
-- TOC entry 3118 (class 0 OID 24900)
-- Dependencies: 220
-- Data for Name: detail_pembelian; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.detail_pembelian (iddetail, idpembelian, idbahan, jumlah, harga_satuan, satuan, subtotal) FROM stdin;
1	1	1	20.00	14000	kg	280000
2	1	2	30.00	2500	butir	75000
3	1	3	20.00	18000	liter	360000
4	1	4	40.00	30000	kg	1200000
5	1	5	12.00	28000	kg	336000
6	2	15	20.00	35000	kg	700000
7	3	1	20.00	14000	kg	280000
8	4	1	12.00	14000	kg	168000
\.


--
-- TOC entry 3099 (class 0 OID 24675)
-- Dependencies: 201
-- Data for Name: inventory; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.inventory ("idBahan", "namaBahan", "hargaBahan", quantity, "kategoriBahan") FROM stdin;
\.


--
-- TOC entry 3100 (class 0 OID 24678)
-- Dependencies: 202
-- Data for Name: kategori; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.kategori (idkategori, namakategori) FROM stdin;
2	Makanan
3	Minuman
\.


--
-- TOC entry 3101 (class 0 OID 24681)
-- Dependencies: 203
-- Data for Name: member; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.member (idmember, namamember, alamat, notelp) FROM stdin;
\.


--
-- TOC entry 3102 (class 0 OID 24684)
-- Dependencies: 204
-- Data for Name: menu; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.menu (idmenu, namamenu, hargamenu, stok, kategori, gambar) FROM stdin;
5	Onion Rings	10000	20	Makanan	images/1761630558406_onion.png
2	Ice Tea	8000	29	Minuman	images/1761628962598_1761111142228_es-teh.jpeg
4	Bullied Rice	20000	20	Makanan	images/1761630062269_1761111339997_Nasi-Kebuli-500x500.jpg
3	Chicken Noodle	15000	40	Makanan	images/1761629797851_1761111374911_mie-ayam.jpg
7	Milk Coffee	10000	0	Minuman	images/1761638164115_1761110985441_kopi-susu.jfif
1	Fried Rice	20000	19	Makanan	images/1761628431330_1761111198930_Nasi-Goreng-telor.jpg
\.


--
-- TOC entry 3104 (class 0 OID 24689)
-- Dependencies: 206
-- Data for Name: metodepembayaran; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.metodepembayaran (idmetode, namametode) FROM stdin;
\.


--
-- TOC entry 3105 (class 0 OID 24692)
-- Dependencies: 207
-- Data for Name: pelayan; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.pelayan (idpelayan, namapelayan, pw, alamat, role, telp, created_at, created_by, status, username) FROM stdin;
1	Johan Liebert	admin123	Pekalongan barat	admin	081234567890	2025-10-28	1	aktif	Johan
2	Light Yagami	kasir123	Pekalongan utara	kasir	089876543210	2025-10-28	1	aktif	Light
3	Kiyotaka Ayanokoji	admin	Pekalongan Selatan	admin	085640016724	2025-10-28	\N	Aktif	ayanokoji
5	namaKasir	kasir123	huhu	kasir	088	2025-10-28	\N	Aktif	kasir
4	namaAdmin	admin123	pekalongan	admin	123	2025-10-28	\N	Aktif	admin
\.


--
-- TOC entry 3116 (class 0 OID 24890)
-- Dependencies: 218
-- Data for Name: pembelian_bahan; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.pembelian_bahan (idpembelian, tanggal, total) FROM stdin;
1	2025-10-28 00:00:00	2251000
2	2025-10-28 00:00:00	700000
3	2025-10-28 00:00:00	280000
4	2025-10-28 00:00:00	168000
\.


--
-- TOC entry 3114 (class 0 OID 24872)
-- Dependencies: 216
-- Data for Name: resep; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.resep (idresep, idmenu, idbahan, jumlah_bahan, satuan) FROM stdin;
1	1	1	0.25	kg
2	1	2	1.00	butir
3	1	3	0.15	liter
4	1	4	0.10	kg
5	1	5	0.12	kg
6	1	15	0.12	kg
7	2	9	1.00	pcs
8	2	14	0.25	liter
9	3	3	0.10	liter
10	3	6	0.10	kg
12	3	8	0.01	kg
13	3	19	0.20	kg
11	3	7	1.00	bungkus
14	3	15	0.25	kg
15	4	1	0.25	kg
16	4	4	0.25	kg
17	4	5	0.20	kg
18	4	11	0.20	botol
19	4	15	0.20	kg
20	4	13	0.10	kg
21	5	4	0.10	kg
22	5	5	0.20	kg
23	5	3	0.25	liter
25	7	10	1.00	bungkus
26	7	23	1.00	Bungkus
27	7	24	0.20	Kg
\.


--
-- TOC entry 3108 (class 0 OID 24762)
-- Dependencies: 210
-- Data for Name: transaksi; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.transaksi (id, no_transaksi, member, nama, agen, pelayan, tgl_transaksi, keterangan, subtotal, diskon, pajak, service, grand_total, tunai, kredit, kembalian, akun_kas) FROM stdin;
1	TRX20251028001	-	Near	Go Food	Johan Liebert	2025-10-28	Kasih Sumpit ya Ayamnya banyakin	20000.00	0.00	11.00	11.00	24400.00	25000.00	0.00	600.00	Uang di Tangan
2	TRX20251028002	-	Near	-	Johan Liebert	2025-10-28		320000.00	0.00	11.00	11.00	390400.00	0.00	400000.00	9600.00	Uang di Tangan
3	TRX20251028003	-	yare yare	-	Johan Liebert	2025-10-28	mantab	28000.00	0.00	11.00	11.00	34160.00	35000.00	0.00	840.00	Uang di Tangan
\.


--
-- TOC entry 3110 (class 0 OID 24784)
-- Dependencies: 212
-- Data for Name: transaksi_detail; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.transaksi_detail (id, id_transaksi, kode_menu, nama_menu, qty, harga, jumlah, status, keterangan) FROM stdin;
1	1	1	Fried Rice	4	80000.00	20000.00		
2	2	1	Fried Rice	16	20000.00	320000.00		enak bang
3	3	1	Fried Rice	1	20000.00	20000.00		
4	3	2	Ice Tea	1	8000.00	8000.00		
\.


--
-- TOC entry 3131 (class 0 OID 0)
-- Dependencies: 213
-- Name: bahan_idbahan_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.bahan_idbahan_seq', 24, true);


--
-- TOC entry 3132 (class 0 OID 0)
-- Dependencies: 219
-- Name: detail_pembelian_iddetail_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.detail_pembelian_iddetail_seq', 8, true);


--
-- TOC entry 3133 (class 0 OID 0)
-- Dependencies: 221
-- Name: kategori_idkategori_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.kategori_idkategori_seq', 4, true);


--
-- TOC entry 3134 (class 0 OID 0)
-- Dependencies: 205
-- Name: menu_idmenu_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.menu_idmenu_seq', 7, true);


--
-- TOC entry 3135 (class 0 OID 0)
-- Dependencies: 208
-- Name: pelayan_idpelayan_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.pelayan_idpelayan_seq', 5, true);


--
-- TOC entry 3136 (class 0 OID 0)
-- Dependencies: 217
-- Name: pembelian_bahan_idpembelian_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.pembelian_bahan_idpembelian_seq', 4, true);


--
-- TOC entry 3137 (class 0 OID 0)
-- Dependencies: 215
-- Name: resep_idresep_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.resep_idresep_seq', 27, true);


--
-- TOC entry 3138 (class 0 OID 0)
-- Dependencies: 211
-- Name: transaksi_detail_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.transaksi_detail_id_seq', 4, true);


--
-- TOC entry 3139 (class 0 OID 0)
-- Dependencies: 209
-- Name: transaksi_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.transaksi_id_seq', 3, true);


--
-- TOC entry 2936 (class 2606 OID 24727)
-- Name: agen agen_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.agen
    ADD CONSTRAINT agen_pkey PRIMARY KEY (idagen);


--
-- TOC entry 2956 (class 2606 OID 24869)
-- Name: bahan bahan_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bahan
    ADD CONSTRAINT bahan_pkey PRIMARY KEY (idbahan);


--
-- TOC entry 2962 (class 2606 OID 24905)
-- Name: detail_pembelian detail_pembelian_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.detail_pembelian
    ADD CONSTRAINT detail_pembelian_pkey PRIMARY KEY (iddetail);


--
-- TOC entry 2938 (class 2606 OID 24729)
-- Name: inventory inventory_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventory
    ADD CONSTRAINT inventory_pkey PRIMARY KEY ("idBahan");


--
-- TOC entry 2940 (class 2606 OID 24731)
-- Name: kategori kategori_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.kategori
    ADD CONSTRAINT kategori_pkey PRIMARY KEY (idkategori);


--
-- TOC entry 2942 (class 2606 OID 24733)
-- Name: member member_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.member
    ADD CONSTRAINT member_pkey PRIMARY KEY (idmember);


--
-- TOC entry 2944 (class 2606 OID 24735)
-- Name: menu menu_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.menu
    ADD CONSTRAINT menu_pkey PRIMARY KEY (idmenu);


--
-- TOC entry 2946 (class 2606 OID 24737)
-- Name: metodepembayaran metodepembayaran_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.metodepembayaran
    ADD CONSTRAINT metodepembayaran_pkey PRIMARY KEY (idmetode);


--
-- TOC entry 2948 (class 2606 OID 24739)
-- Name: pelayan pelayan_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pelayan
    ADD CONSTRAINT pelayan_pkey PRIMARY KEY (idpelayan);


--
-- TOC entry 2960 (class 2606 OID 24897)
-- Name: pembelian_bahan pembelian_bahan_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pembelian_bahan
    ADD CONSTRAINT pembelian_bahan_pkey PRIMARY KEY (idpembelian);


--
-- TOC entry 2958 (class 2606 OID 24877)
-- Name: resep resep_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resep
    ADD CONSTRAINT resep_pkey PRIMARY KEY (idresep);


--
-- TOC entry 2954 (class 2606 OID 24792)
-- Name: transaksi_detail transaksi_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transaksi_detail
    ADD CONSTRAINT transaksi_detail_pkey PRIMARY KEY (id);


--
-- TOC entry 2950 (class 2606 OID 24781)
-- Name: transaksi transaksi_no_transaksi_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transaksi
    ADD CONSTRAINT transaksi_no_transaksi_key UNIQUE (no_transaksi);


--
-- TOC entry 2952 (class 2606 OID 24779)
-- Name: transaksi transaksi_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transaksi
    ADD CONSTRAINT transaksi_pkey PRIMARY KEY (id);


--
-- TOC entry 2967 (class 2606 OID 24911)
-- Name: detail_pembelian detail_pembelian_idbahan_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.detail_pembelian
    ADD CONSTRAINT detail_pembelian_idbahan_fkey FOREIGN KEY (idbahan) REFERENCES public.bahan(idbahan);


--
-- TOC entry 2966 (class 2606 OID 24906)
-- Name: detail_pembelian detail_pembelian_idpembelian_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.detail_pembelian
    ADD CONSTRAINT detail_pembelian_idpembelian_fkey FOREIGN KEY (idpembelian) REFERENCES public.pembelian_bahan(idpembelian) ON DELETE CASCADE;


--
-- TOC entry 2965 (class 2606 OID 24883)
-- Name: resep resep_idbahan_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resep
    ADD CONSTRAINT resep_idbahan_fkey FOREIGN KEY (idbahan) REFERENCES public.bahan(idbahan) ON DELETE CASCADE;


--
-- TOC entry 2964 (class 2606 OID 24878)
-- Name: resep resep_idmenu_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resep
    ADD CONSTRAINT resep_idmenu_fkey FOREIGN KEY (idmenu) REFERENCES public.menu(idmenu) ON DELETE CASCADE;


--
-- TOC entry 2963 (class 2606 OID 24793)
-- Name: transaksi_detail transaksi_detail_id_transaksi_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transaksi_detail
    ADD CONSTRAINT transaksi_detail_id_transaksi_fkey FOREIGN KEY (id_transaksi) REFERENCES public.transaksi(id) ON DELETE CASCADE;


-- Completed on 2025-10-28 17:48:18

--
-- PostgreSQL database dump complete
--

\unrestrict P3bmmyLThrFEabxBRe5i3xTumXtKn8eG9XO4apdzv9b1JG18cLUNqFx6gNLO7UP

