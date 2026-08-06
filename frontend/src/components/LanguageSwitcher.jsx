import { Select } from "@base-ui/react/select"
import { Check, ChevronDown, Languages } from "lucide-react"
import { useTranslation } from "react-i18next"

const languages = [
  { value: "es", label: "Español" },
  { value: "en", label: "English" },
]

export function LanguageSwitcher() {
  const { i18n } = useTranslation()

  const current = i18n.resolvedLanguage || i18n.language || "es"

  return (
    <Select.Root
      value={current}
      onValueChange={(value) => i18n.changeLanguage(value)}
    >
      <Select.Trigger className="group/select inline-flex h-8 items-center gap-1.5 rounded-lg border border-border bg-background px-2.5 text-sm font-medium text-foreground outline-none transition-all hover:bg-muted focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50 data-[popup-open]:bg-muted">
        <Languages className="size-4" />
        <span className="uppercase text-xs font-semibold">{current}</span>
        <ChevronDown className="size-3.5 text-muted-foreground transition-transform group-data-[popup-open]/select:rotate-180" />
      </Select.Trigger>

      <Select.Portal>
        <Select.Positioner sideOffset={8} align="end">
          <Select.Popup className="z-50 min-w-36 overflow-hidden rounded-lg border border-border bg-popover p-1 text-popover-foreground shadow-lg">
            {languages.map((lang) => (
              <Select.Item
                key={lang.value}
                value={lang.value}
                className="flex cursor-pointer items-center justify-between gap-6 rounded-md px-2.5 py-2 text-sm outline-none select-none data-highlighted:bg-muted data-selected:bg-primary/10 data-selected:font-medium"
              >
                <Select.ItemText>{lang.label}</Select.ItemText>
                <Select.ItemIndicator>
                  <Check className="size-4 text-primary" />
                </Select.ItemIndicator>
              </Select.Item>
            ))}
          </Select.Popup>
        </Select.Positioner>
      </Select.Portal>
    </Select.Root>
  )
}
