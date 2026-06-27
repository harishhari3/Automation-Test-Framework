# Deployment Guide - Going Live with Cosmic Goods Storefront

Since the application contains a Node.js backend server (`server.js`) that queries a local JSON database (`db.json`), you can deploy it to the cloud. Here are the two best ways to publish it live for free without any failures.

---

## Option 1: Render.com (Recommended - Easiest & 100% Failure-Free)

Render natively supports persistent Node.js servers, making it the simplest choice.

### Step 1: Initialize Git and Push to GitHub
1. Create a new repository on **GitHub** (e.g., `cosmic-goods`).
2. Open a terminal in the project directory (`c:\Users\HP\Documents\selenium`) and run:
   ```bash
   git init
   git add webapp/ db.json server.js package.json
   git commit -m "feat: initial release of cosmic goods full-stack app"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/cosmic-goods.git
   git push -u origin main
   ```

### Step 2: Deploy to Render
1. Create a free account on **[Render.com](https://render.com/)**.
2. Click **New +** and select **Web Service**.
3. Connect your GitHub account and select your `cosmic-goods` repository.
4. Fill in the service configuration:
   - **Name:** `cosmic-goods`
   - **Environment:** `Node`
   - **Build Command:** *(Leave blank or set to `npm install` if any dependencies are added later)*
   - **Start Command:** `node server.js`
   - **Instance Type:** `Free`
5. Click **Deploy Web Service**. Render will build the site and give you a public URL (e.g., `https://cosmic-goods.onrender.com`).

---

## Option 2: Vercel (Fastest Static + Serverless Edge Delivery)

Vercel is designed for serverless functions and static sites. To deploy the Node.js server seamlessly on Vercel without altering the code structure, we must define routing and builder rules using a `vercel.json` file.

### Step 1: Create `vercel.json`
We will configure Vercel to route backend endpoints to the server script, and direct other traffic to serve static webapp folder files:

```json
{
  "version": 2,
  "builds": [
    {
      "src": "server.js",
      "use": "@vercel/node"
    },
    {
      "src": "webapp/**/*",
      "use": "@vercel/static"
    }
  ],
  "routes": [
    {
      "src": "/api/(.*)",
      "dest": "server.js"
    },
    {
      "src": "/(.*)",
      "dest": "webapp/$1"
    }
  ]
}
```

### Step 2: Deploy using Vercel CLI
1. Open a terminal in your workspace and install Vercel CLI globally:
   ```bash
   npm install -g vercel
   ```
2. Run the deployment command:
   ```bash
   vercel
   ```
3. Follow the interactive prompts to link the project.
4. Deploy to production using:
   ```bash
   vercel --prod
   ```

---

## Option 3: Railway.app (Zero-Config alternative to Render)

Railway is another excellent service that handles standard Node servers automatically.

1. Connect GitHub repository to **[Railway.app](https://railway.app/)**.
2. Select **New Project** -> **Deploy from GitHub**.
3. Railway automatically detects `server.js` and deploys it.
4. Under project Settings, click **Generate Domain** to get your live URL.
