export const e2eWorkflowYaml = `name: Scale E2E Suites to 1800 Test Cases with Robust Selenium/Appium Fallback

on:
  push:
    branches: [ main, dev ]
  pull_request:
    branches: [ main ]

jobs:
  unit-tests-api:
    name: Unit Tests — API (300)
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'

      - name: Install Dependencies
        run: npm ci

      - name: Run API Unit Tests (300 cases)
        run: npm run test:api -- --reporter=json
        env:
          TEST_SUITE: api

  selenium-website-tests:
    name: Selenium — Website Tests (300)
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4

      - name: Setup Python
        uses: actions/setup-python@v5
        with:
          python-version: '3.11'

      - name: Setup Chrome/ChromeDriver
        uses: browser-actions/setup-chrome@v1

      - name: Run Selenium Suites (300 cases)
        run: |
          pip install selenium pytest
          pytest tests/selenium -v --html=report.html --self-contained-html
        continue-on-error: true # Enable fallback configuration

      - name: Selenium Fallback Trigger (If Headless Fails)
        if: failure()
        run: |
          echo "Headless execution failed. Launching Virtual Framebuffer (Xvfb) for fallback execution..."
          sudo apt-get install xvfb
          xvfb-run pytest tests/selenium -v --html=report_fallback.html

      - name: Upload Selenium Artifacts
        uses: actions/upload-artifact@v4
        with:
          name: selenium-screenshots
          path: screenshots/

  appium-android-tests:
    name: Appium — Android Tests (300)
    runs-on: macos-13
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4

      - name: Setup Java JDK
        uses: actions/setup-java@v4
        with:
          distribution: 'zulu'
          java-version: '17'

      - name: Run Appium Android Emulator Tests (300 cases)
        run: |
          npm install -g appium
          appium driver install uiautomator2
          bash scripts/run-emulator-tests.sh
        continue-on-error: true

      - name: Appium Firebase Test Lab Fallback
        if: failure()
        run: |
          echo "Local emulator run failed. Falling back to Cloud Testing Suites..."
          gcloud firebase test android run --app=app/build/outputs/apk/debug/app-debug.apk --test=app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

  validation-tests:
    name: Validation Tests (300)
    runs-on: ubuntu-latest
    needs: [unit-tests-api]
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4

      - name: Run Validation Checks (300 cases)
        run: npm run validate:all

  load-testing-performance:
    name: Load Testing — Performance (300)
    runs-on: ubuntu-latest
    needs: [unit-tests-api]
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4

      - name: Run k6 Load Testing (300 endpoints)
        run: docker run --rm -i grafana/k6 run - <tests/load/k6-script.js

  deployment-status:
    name: Deployment Status (300)
    runs-on: ubuntu-latest
    needs: [validation-tests]
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4

      - name: Verify Cloud Target Status (300 checks)
        run: npm run verify:deployment

  compile-master-report:
    name: Compile Master Report & Deploy
    runs-on: ubuntu-latest
    needs: [selenium-website-tests, appium-android-tests, validation-tests, load-testing-performance, deployment-status]
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4

      - name: Fetch and Merge Test Artifacts
        uses: actions/download-artifact@v4

      - name: Run Report Compiler Script
        run: node scripts/compile-reports.js

      - name: Generate PDF and HTML Reports
        run: npm run generate-reports -- --pdf --html

      - name: Deploy E2E Dashboard
        run: |
          echo "Deploying execution metrics dashboard..."
          npm run deploy:dashboard
`;
