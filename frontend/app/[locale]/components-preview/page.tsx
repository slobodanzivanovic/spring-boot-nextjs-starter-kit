"use client";

import Button from "@/components/common/Button";
import Card from "@/components/common/Card";
import Container from "@/components/common/Container";
import {ThemeToggle} from "@/components/ThemeToggle";
import {useState} from "react";

import styles from "./ComponentsPreview.module.css";

const containerSizes = ["small", "medium", "large", "full"] as const;

export default function ComponentsPreview() {
  const [showCardTitle, setShowCardTitle] = useState(true);
  const [showCardSubtitle, setShowCardSubtitle] = useState(true);
  const [showCardFooter, setShowCardFooter] = useState(true);

  const [containerSizeIndex, setContainerSizeIndex] = useState(1);
  const [containerCentered, setContainerCentered] = useState(false);
  const [containerPadded, setContainerPadded] = useState(true);

  const currentContainerSize = containerSizes[containerSizeIndex];

  const cycleContainerSize = () => {
    setContainerSizeIndex(prev => (prev + 1) % containerSizes.length);
  };

  return (
    <div className={styles.outerContainer}>
      <h1>Components Preview Page</h1>

      <div className={styles.componentsContainer}>
        {/* ThemeToggle Preview */}
        <div className={styles.componentContainer}>
          <h2>Theme Toggle Component</h2>
          <div className={styles.controls}>
            <ThemeToggle />
          </div>
        </div>

        {/* Button Preview */}
        <div className={styles.componentContainer}>
          <h2>Button Component</h2>
          <div className={styles.buttonContainer}>
            <Button onClick={() => alert("Primary clicked")}>Primary</Button>
            <Button
              variant="secondary"
              onClick={() => alert("Secondary clicked")}
            >
              Secondary
            </Button>
            <Button variant="danger" onClick={() => alert("Danger clicked")}>
              Danger
            </Button>
            <Button variant="text" onClick={() => alert("Text clicked")}>
              Text
            </Button>
            <Button variant="oauth" onClick={() => alert("OAuth clicked")}>
              OAuth
            </Button>

            <Button size="small" onClick={() => alert("Small clicked")}>
              Small
            </Button>
            <Button size="medium" onClick={() => alert("Medium clicked")}>
              Medium
            </Button>
            <Button size="large" onClick={() => alert("Large clicked")}>
              Large
            </Button>

            <Button isLoading onClick={() => {}}>
              Loading...
            </Button>

            <Button disabled onClick={() => alert("Should not fire")}>
              Disabled
            </Button>

            <Button fullWidth onClick={() => alert("Full width clicked")}>
              Full Width
            </Button>
          </div>
        </div>

        {/* Card Preview */}
        <div className={styles.componentContainer}>
          <h2>Card Component</h2>
          <div className={styles.buttons}>
            <Button size="small" onClick={() => setShowCardTitle(t => !t)}>
              Toggle Title ({showCardTitle ? "On" : "Off"})
            </Button>
            <Button size="small" onClick={() => setShowCardSubtitle(s => !s)}>
              Toggle Subtitle ({showCardSubtitle ? "On" : "Off"})
            </Button>
            <Button size="small" onClick={() => setShowCardFooter(f => !f)}>
              Toggle Footer ({showCardFooter ? "On" : "Off"})
            </Button>
          </div>

          <Card
            title={showCardTitle ? "Card Title" : null}
            subtitle={showCardSubtitle ? "Card Subtitle" : null}
            footer={showCardFooter ? <div>Footer content here</div> : null}
          >
            <p>This is the main content of the Card component.</p>
          </Card>
        </div>

        {/* Container Preview */}
        <div className={styles.componentContainer}>
          <h2>Container Component</h2>
          <div className={styles.buttons}>
            <Button size="small" onClick={cycleContainerSize}>
              Change Size ({currentContainerSize})
            </Button>
            <Button size="small" onClick={() => setContainerCentered(c => !c)}>
              Toggle Centered ({containerCentered ? "Yes" : "No"})
            </Button>
            <Button size="small" onClick={() => setContainerPadded(p => !p)}>
              Toggle Padded ({containerPadded ? "Yes" : "No"})
            </Button>
          </div>

          <Container
            size={currentContainerSize}
            centered={containerCentered}
            padded={containerPadded}
            className={styles.containerTest}
          >
            <p>
              This is a preview content inside the Container component with size{" "}
              <b>{currentContainerSize}</b>, centered:{" "}
              <b>{containerCentered ? "Yes" : "No"}</b>, padded:{" "}
              <b>{containerPadded ? "Yes" : "No"}</b>.
            </p>
          </Container>
        </div>
      </div>
    </div>
  );
}
