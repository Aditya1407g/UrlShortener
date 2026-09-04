# Deployment configs

These files describe the production deployment for the URL Shortener on AWS EC2.

## Files

- `url-shortener.service` — systemd unit file for auto-managing the Spring Boot process
- `url-shortener.nginx.conf` — nginx reverse proxy config, listens on 80/443, forwards to Spring Boot on 8080
- `index.html` — static landing page served by nginx at the root URL only

## Setup on a fresh Ubuntu 26.04 server

1. Install Java 21: `sudo apt install -y openjdk-21-jdk`
2. Install Postgres 18+: `sudo apt install -y postgresql postgresql-contrib`
3. Install nginx: `sudo apt install -y nginx`
4. Install certbot: `sudo apt install -y certbot python3-certbot-nginx`
5. Create database + app user (see main README)
6. Copy `url-shortener.service` to `/etc/systemd/system/`, edit the Environment variables with real secrets
7. Copy `url-shortener.nginx.conf` to `/etc/nginx/sites-available/url-shortener`, symlink to `sites-enabled/`
8. Get HTTPS cert: `sudo certbot --nginx -d <your-domain>`
9. `sudo systemctl enable url-shortener` and `sudo systemctl start url-shortener`

### Deploy the landing page

Copy both files up first — after you `ssh` in you are on the server, where the
repo's `deploy/` directory does not exist, so the server commands must reference
the uploaded copies in `/tmp/`.

**From your local machine, in the repo root:**

```bash
scp deploy/index.html deploy/url-shortener.nginx.conf ubuntu@<host>:/tmp/
```

**Then on the server:**

```bash
ssh ubuntu@<host>
sudo mkdir -p /var/www/urlshortener
sudo mv /tmp/index.html /var/www/urlshortener/
sudo chown -R www-data:www-data /var/www/urlshortener
sudo cp /tmp/url-shortener.nginx.conf /etc/nginx/sites-available/url-shortener
sudo nginx -t && sudo systemctl reload nginx
```

## Live at
https://go.adityag.dev
