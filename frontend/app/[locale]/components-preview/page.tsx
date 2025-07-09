"use client";

import Alert, {AlertVariant} from "@/components/common/Alert";
import Avatar, {AvatarSize} from "@/components/common/Avatar";
import Badge from "@/components/common/Badge";
import Button from "@/components/common/Button";
import Card from "@/components/common/Card";
import Container from "@/components/common/Container";
import Divider from "@/components/common/Divider";
import Modal from "@/components/common/Modal";
import Spinner, {SpinnerColor, SpinnerSize} from "@/components/common/Spinner";
import {ThemeToggle} from "@/components/ThemeToggle";
import {useState} from "react";

import styles from "./ComponentsPreview.module.css";

const alertVariants: AlertVariant[] = ["info", "success", "warning", "error"];
const avatarSizes: AvatarSize[] = ["small", "medium", "large", "xlarge"];
const containerSizes = ["small", "medium", "large", "full"] as const;

const spinnerSizes: SpinnerSize[] = ["small", "medium", "large"];
const spinnerColors: SpinnerColor[] = ["primary", "white", "dark"];

export default function ComponentsPreview() {
  const [alertIndex, setAlertIndex] = useState(0);
  const [avatarIndex, setAvatarIndex] = useState(0);
  const [avatarMode, setAvatarMode] = useState<"image" | "name" | "empty">(
    "image",
  );
  const [showAlert, setShowAlert] = useState(true);

  const [showCardTitle, setShowCardTitle] = useState(true);
  const [showCardSubtitle, setShowCardSubtitle] = useState(true);
  const [showCardFooter, setShowCardFooter] = useState(true);

  const [dividerText, setDividerText] = useState("Divider Text");
  const [dividerOrientation, setDividerOrientation] = useState<
    "horizontal" | "vertical"
  >("horizontal");

  const [containerSizeIndex, setContainerSizeIndex] = useState(1);
  const [containerCentered, setContainerCentered] = useState(false);
  const [containerPadded, setContainerPadded] = useState(true);

  const [spinnerSizeIndex, setSpinnerSizeIndex] = useState(1);
  const [spinnerColorIndex, setSpinnerColorIndex] = useState(0);
  const [spinnerFullPage, setSpinnerFullPage] = useState(false);

  const [isModalOpen, setIsModalOpen] = useState(false);

  const currentAlertVariant = alertVariants[alertIndex];
  const currentAvatarSize = avatarSizes[avatarIndex];
  const currentContainerSize = containerSizes[containerSizeIndex];

  const handleChangeAlertVariant = () => {
    setAlertIndex(prev => (prev + 1) % alertVariants.length);
    setShowAlert(true);
  };

  const handleCycleAvatarSize = () => {
    setAvatarIndex(prev => (prev + 1) % avatarSizes.length);
  };

  const handleToggleAvatarMode = () => {
    setAvatarMode(prev =>
      prev === "image" ? "name" : prev === "name" ? "empty" : "image",
    );
  };

  const toggleDividerOrientation = () => {
    setDividerOrientation(prev =>
      prev === "horizontal" ? "vertical" : "horizontal",
    );
  };

  const cycleContainerSize = () => {
    setContainerSizeIndex(prev => (prev + 1) % containerSizes.length);
  };

  const openModal = () => setIsModalOpen(true);
  const closeModal = () => setIsModalOpen(false);

  const currentSpinnerSize = spinnerSizes[spinnerSizeIndex];
  const currentSpinnerColor = spinnerColors[spinnerColorIndex];

  const cycleSpinnerSize = () =>
    setSpinnerSizeIndex(prev => (prev + 1) % spinnerSizes.length);

  const cycleSpinnerColor = () =>
    setSpinnerColorIndex(prev => (prev + 1) % spinnerColors.length);

  const toggleSpinnerFullPage = () => setSpinnerFullPage(prev => !prev);

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

        {/* Alert Preview */}
        <div className={styles.componentContainer}>
          <h2>Alert Component</h2>
          <Button size="small" onClick={handleChangeAlertVariant}>
            Change variant ({currentAlertVariant})
          </Button>
          {showAlert && (
            <Alert
              variant={currentAlertVariant}
              title="Preview Alert"
              onClose={() => setShowAlert(false)}
              className={styles.alertContainer}
            >
              <p>
                <strong>{currentAlertVariant?.toUpperCase()}:</strong> Alert
                message for Preview.
              </p>
            </Alert>
          )}
        </div>

        {/* Avatar Preview */}
        <div className={styles.componentContainer}>
          <h2>Avatar Component</h2>
          <div className={styles.avatarContainer}>
            <Avatar
              size={currentAvatarSize}
              name={avatarMode === "name" ? "Slobodan Zivanovic" : ""}
              src={
                avatarMode === "image"
                  ? "https://s3.tebi.io/programiraj/user%3A9890cc41-f6b3-46a5-bad3-ccbf088b224b/profile/bddb9fcb-6053-4962-b132-1857e29a0deb.png"
                  : undefined
              }
              onClick={() => alert("Avatar clicked")}
            />
            <div className={styles.buttons}>
              <Button size="small" onClick={handleCycleAvatarSize}>
                Change size ({currentAvatarSize})
              </Button>
              <Button size="small" onClick={handleToggleAvatarMode}>
                Toggle mode ({avatarMode})
              </Button>
            </div>
          </div>
        </div>

        {/* Badge Preview */}
        <div className={styles.componentContainer}>
          <h2>Badge Component</h2>
          <div className={styles.badgeContainer}>
            <Badge variant="primary" size="small" rounded>
              Primary Small
            </Badge>
            <Badge variant="secondary" size="medium" rounded>
              Secondary Medium
            </Badge>
            <Badge variant="success" size="large">
              Success Large
            </Badge>
            <Badge variant="error" size="medium" rounded>
              Error Medium
            </Badge>
            <Badge variant="warning" size="small">
              Warning Small
            </Badge>
            <Badge variant="info" size="medium" rounded>
              Info Medium
            </Badge>
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

        {/* Divider Preview */}
        <div className={styles.componentContainer}>
          <h2>Divider Component</h2>
          <div className={styles.buttons}>
            <Button size="small" onClick={toggleDividerOrientation}>
              Toggle Orientation ({dividerOrientation})
            </Button>
            <Button
              size="small"
              onClick={() =>
                setDividerText(prev => (prev ? "" : "Divider Text"))
              }
            >
              Toggle Text ({dividerText ? "On" : "Off"})
            </Button>
          </div>

          <div className={styles.dividerContainer}>
            <span>Content 1</span>
            <Divider
              orientation={dividerOrientation}
              text={dividerText || undefined}
              className={styles.dividerPreview}
              textClassName={styles.dividerTextPreview}
            />
            <span>Content 2</span>
          </div>
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
            className={styles.containerPreview}
          >
            <p>
              This is a Preview content inside the Container component with size{" "}
              <b>{currentContainerSize}</b>, centered:{" "}
              <b>{containerCentered ? "Yes" : "No"}</b>, padded:{" "}
              <b>{containerPadded ? "Yes" : "No"}</b>.
            </p>
          </Container>
        </div>

        {/* TODO: fix error on first opening */}
        {/* Modal Preview */}
        <div className={styles.componentContainer}>
          <h2>Modal Component</h2>
          <Button onClick={openModal}>Open Modal</Button>

          <Modal
            isOpen={isModalOpen}
            onClose={closeModal}
            title="Preview Modal"
            footer={
              <div className={styles.buttons}>
                <Button onClick={closeModal}>Close</Button>
                <Button onClick={() => alert("Action!")}>Action</Button>
              </div>
            }
            size="medium"
          >
            <p>This is a Preview modal content.</p>
          </Modal>
        </div>

        {/* Spinner Preview */}
        <div className={styles.componentContainer}>
          <h2>Spinner Component</h2>
          <div className={styles.buttons}>
            <Button size="small" onClick={cycleSpinnerSize}>
              Change Size ({currentSpinnerSize})
            </Button>
            <Button size="small" onClick={cycleSpinnerColor}>
              Change Color ({currentSpinnerColor})
            </Button>
            <Button size="small" onClick={toggleSpinnerFullPage}>
              Toggle Full Page ({spinnerFullPage ? "On" : "Off"})
            </Button>
          </div>

          <Spinner
            size={currentSpinnerSize}
            color={currentSpinnerColor}
            fullPage={spinnerFullPage}
            label="Loading..."
          />
        </div>
      </div>
    </div>
  );
}
