// Minimal service worker for the PNGUOT Student & Staff Support Hub.
//
// Deliberately does NOT cache pages or API responses — this app handles
// confidential case data, so every page load and API call always goes to
// the network. This service worker exists purely to make the app
// installable (the "Add to Home Screen" prompt requires one), not to
// provide offline functionality.

const CACHE_NAME = 'support-hub-static-v1';
const STATIC_ASSETS = [
  '/manifest.json',
  '/icons/icon-192.png',
  '/icons/icon-512.png',
  '/images/pnguot-logo.jpg'
];

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => cache.addAll(STATIC_ASSETS))
  );
  self.skipWaiting();
});

self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(keys.filter((k) => k !== CACHE_NAME).map((k) => caches.delete(k)))
    )
  );
  self.clients.claim();
});

self.addEventListener('fetch', (event) => {
  const url = new URL(event.request.url);
  // Only ever serve these specific static assets from cache when offline.
  // Every page navigation and every /api/* call always hits the network.
  if (STATIC_ASSETS.includes(url.pathname)) {
    event.respondWith(
      caches.match(event.request).then((cached) => cached || fetch(event.request))
    );
  }
});
